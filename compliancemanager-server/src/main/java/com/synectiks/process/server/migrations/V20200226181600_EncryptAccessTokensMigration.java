/*
 * */
package com.synectiks.process.server.migrations;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.synectiks.process.server.database.MongoConnection;
import com.synectiks.process.server.security.AccessTokenCipher;
import com.synectiks.process.server.security.AccessTokenImpl;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.ZonedDateTime;

import static com.google.common.base.Strings.isNullOrEmpty;

public class V20200226181600_EncryptAccessTokensMigration extends Migration {
    private static final Logger LOG = LoggerFactory.getLogger(V20200226181600_EncryptAccessTokensMigration.class);

    private final MongoConnection mongoConnection;
    private final AccessTokenCipher accessTokenCipher;

    @Inject
    public V20200226181600_EncryptAccessTokensMigration(AccessTokenCipher accessTokenCipher, MongoConnection mongoConnection) {
        this.accessTokenCipher = accessTokenCipher;
        this.mongoConnection = mongoConnection;
    }

    @Override
    public ZonedDateTime createdAt() {
        return ZonedDateTime.parse("2020-02-26T18:16:00Z");
    }

    @Override
    public void upgrade() {
        final MongoCollection<Document> collection = mongoConnection.getMongoDatabase().getCollection(AccessTokenImpl.COLLECTION_NAME);

        // We use the absence of the "token_type" field as an indicator to select access tokens that need to be encrypted
        // If we should change the encryption method in the future, we need to adjust the query
        for (final Document document : collection.find(Filters.exists(AccessTokenImpl.TOKEN_TYPE, false))) {
            final String tokenId = document.getObjectId("_id").toHexString();
            final String tokenName = document.getString(AccessTokenImpl.NAME);
            final String tokenUsername = document.getString(AccessTokenImpl.USERNAME);
            final String tokenValue = document.getString(AccessTokenImpl.TOKEN);

            if (isNullOrEmpty(tokenValue)) {
                LOG.warn("Couldn't encrypt empty value for access token <{}/{}> of user <{}>", tokenId, tokenName, tokenUsername);
                continue;
            }

            final Bson query = Filters.eq("_id", document.getObjectId("_id"));
            final Bson updates = Updates.combine(
                    Updates.set(AccessTokenImpl.TOKEN_TYPE, AccessTokenImpl.Type.AES_SIV.getIntValue()),
                    Updates.set(AccessTokenImpl.TOKEN, accessTokenCipher.encrypt(tokenValue))
            );

            LOG.info("Encrypting access token <{}/{}> for user <{}>", tokenId, tokenName, tokenUsername);
            final UpdateResult result = collection.updateOne(query, updates);
            if (result.getModifiedCount() != 1) {
                LOG.warn("Expected to modify one access token, but <{}> have been updated", result.getModifiedCount());
            }
        }
    }
}
