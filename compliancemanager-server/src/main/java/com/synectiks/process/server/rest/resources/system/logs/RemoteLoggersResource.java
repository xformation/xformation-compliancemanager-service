/*
 * */
package com.synectiks.process.server.rest.resources.system.logs;

import com.synectiks.process.server.rest.models.system.loggers.responses.LogMessagesSummary;
import com.synectiks.process.server.rest.models.system.loggers.responses.LoggersSummary;
import com.synectiks.process.server.rest.models.system.loggers.responses.SubsystemSummary;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RemoteLoggersResource {
    @GET("system/loggers")
    Call<LoggersSummary> loggers();

    @GET("system/loggers/subsystems")
    Call<SubsystemSummary> subsystems();

    @PUT("system/loggers/subsystems/{subsystem}/level/{level}")
    Call<Void> setSubsystemLoggerLevel(@Path("subsystem") String subsystemTitle, @Path("level") String level);

    @PUT("system/loggers/{loggerName}/level/{level}")
    Call<Void> setSingleLoggerLevel(String loggerName, String level);

    @GET("system/loggers/messages/recent")
    Call<LogMessagesSummary> messages(int limit, String level);
}
