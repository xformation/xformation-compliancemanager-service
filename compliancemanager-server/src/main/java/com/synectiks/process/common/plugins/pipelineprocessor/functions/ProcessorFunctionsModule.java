/*
 * */
package com.synectiks.process.common.plugins.pipelineprocessor.functions;

import com.google.inject.Binder;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.synectiks.process.common.plugins.pipelineprocessor.ast.functions.Function;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.conversion.BooleanConversion;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.conversion.DoubleConversion;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.conversion.IsBoolean;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.conversion.IsCollection;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.conversion.IsDouble;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.conversion.IsList;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.conversion.IsLong;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.conversion.IsMap;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.conversion.IsNumber;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.conversion.IsString;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.conversion.LongConversion;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.conversion.MapConversion;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.conversion.StringConversion;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.dates.DateConversion;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.dates.FlexParseDate;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.dates.FormatDate;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.dates.IsDate;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.dates.Now;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.dates.ParseDate;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.dates.ParseUnixMilliseconds;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.dates.periods.Days;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.dates.periods.Hours;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.dates.periods.IsPeriod;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.dates.periods.Millis;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.dates.periods.Minutes;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.dates.periods.Months;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.dates.periods.PeriodParseFunction;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.dates.periods.Seconds;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.dates.periods.Weeks;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.dates.periods.Years;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.debug.Debug;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.debug.MetricCounterIncrement;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.encoding.Base16Decode;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.encoding.Base16Encode;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.encoding.Base32Decode;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.encoding.Base32Encode;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.encoding.Base32HumanDecode;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.encoding.Base32HumanEncode;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.encoding.Base64Decode;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.encoding.Base64Encode;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.encoding.Base64UrlDecode;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.encoding.Base64UrlEncode;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.hashing.CRC32;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.hashing.CRC32C;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.hashing.MD5;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.hashing.Murmur3_128;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.hashing.Murmur3_32;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.hashing.SHA1;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.hashing.SHA256;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.hashing.SHA512;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.ips.CidrMatch;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.ips.IpAddressConversion;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.ips.IsIp;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.json.IsJson;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.json.JsonParse;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.json.SelectJsonPath;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.lookup.Lookup;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.lookup.LookupAddStringList;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.lookup.LookupClearKey;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.lookup.LookupRemoveStringList;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.lookup.LookupSetStringList;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.lookup.LookupSetValue;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.lookup.LookupStringList;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.lookup.LookupValue;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.messages.CloneMessage;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.messages.CreateMessage;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.messages.DropMessage;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.messages.HasField;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.messages.RemoveField;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.messages.RemoveFromStream;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.messages.RenameField;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.messages.RouteToStream;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.messages.SetField;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.messages.SetFields;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.messages.StreamCacheService;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.messages.TrafficAccountingSize;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.strings.Abbreviate;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.strings.Capitalize;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.strings.Concat;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.strings.Contains;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.strings.EndsWith;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.strings.FirstNonNull;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.strings.GrokMatch;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.strings.Join;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.strings.KeyValue;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.strings.Length;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.strings.Lowercase;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.strings.RegexMatch;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.strings.RegexReplace;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.strings.Replace;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.strings.Split;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.strings.StartsWith;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.strings.Substring;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.strings.Swapcase;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.strings.Uncapitalize;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.strings.Uppercase;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.syslog.SyslogFacilityConversion;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.syslog.SyslogLevelConversion;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.syslog.SyslogPriorityConversion;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.syslog.SyslogPriorityToStringConversion;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.urls.IsUrl;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.urls.UrlConversion;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.urls.UrlDecode;
import com.synectiks.process.common.plugins.pipelineprocessor.functions.urls.UrlEncode;
import com.synectiks.process.server.plugin.PluginModule;

public class ProcessorFunctionsModule extends PluginModule {
    @Override
    protected void configure() {
        // built-in functions
        addMessageProcessorFunction(BooleanConversion.NAME, BooleanConversion.class);
        addMessageProcessorFunction(DoubleConversion.NAME, DoubleConversion.class);
        addMessageProcessorFunction(LongConversion.NAME, LongConversion.class);
        addMessageProcessorFunction(StringConversion.NAME, StringConversion.class);
        addMessageProcessorFunction(MapConversion.NAME, MapConversion.class);

        // Comparison functions
        addMessageProcessorFunction(IsBoolean.NAME, IsBoolean.class);
        addMessageProcessorFunction(IsNumber.NAME, IsNumber.class);
        addMessageProcessorFunction(IsDouble.NAME, IsDouble.class);
        addMessageProcessorFunction(IsLong.NAME, IsLong.class);
        addMessageProcessorFunction(IsString.NAME, IsString.class);
        addMessageProcessorFunction(IsCollection.NAME, IsCollection.class);
        addMessageProcessorFunction(IsList.NAME, IsList.class);
        addMessageProcessorFunction(IsMap.NAME, IsMap.class);
        addMessageProcessorFunction(IsDate.NAME, IsDate.class);
        addMessageProcessorFunction(IsPeriod.NAME, IsPeriod.class);
        addMessageProcessorFunction(IsIp.NAME, IsIp.class);
        addMessageProcessorFunction(IsJson.NAME, IsJson.class);
        addMessageProcessorFunction(IsUrl.NAME, IsUrl.class);

        // message related functions
        addMessageProcessorFunction(HasField.NAME, HasField.class);
        addMessageProcessorFunction(SetField.NAME, SetField.class);
        addMessageProcessorFunction(SetFields.NAME, SetFields.class);
        addMessageProcessorFunction(RenameField.NAME, RenameField.class);
        addMessageProcessorFunction(RemoveField.NAME, RemoveField.class);

        addMessageProcessorFunction(DropMessage.NAME, DropMessage.class);
        addMessageProcessorFunction(CreateMessage.NAME, CreateMessage.class);
        addMessageProcessorFunction(CloneMessage.NAME, CloneMessage.class);
        addMessageProcessorFunction(RemoveFromStream.NAME, RemoveFromStream.class);
        addMessageProcessorFunction(RouteToStream.NAME, RouteToStream.class);
        addMessageProcessorFunction(TrafficAccountingSize.NAME, TrafficAccountingSize.class);
        // helper service for route_to_stream
        serviceBinder().addBinding().to(StreamCacheService.class).in(Scopes.SINGLETON);

        // input related functions
        addMessageProcessorFunction(FromInput.NAME, FromInput.class);

        // generic functions
        addMessageProcessorFunction(RegexMatch.NAME, RegexMatch.class);
        addMessageProcessorFunction(RegexReplace.NAME, RegexReplace.class);
        addMessageProcessorFunction(GrokMatch.NAME, GrokMatch.class);
        addMessageProcessorFunction(GrokExists.NAME, GrokExists.class);

        // string functions
        addMessageProcessorFunction(Abbreviate.NAME, Abbreviate.class);
        addMessageProcessorFunction(Capitalize.NAME, Capitalize.class);
        addMessageProcessorFunction(Contains.NAME, Contains.class);
        addMessageProcessorFunction(EndsWith.NAME, EndsWith.class);
        addMessageProcessorFunction(Lowercase.NAME, Lowercase.class);
        addMessageProcessorFunction(Substring.NAME, Substring.class);
        addMessageProcessorFunction(Swapcase.NAME, Swapcase.class);
        addMessageProcessorFunction(Uncapitalize.NAME, Uncapitalize.class);
        addMessageProcessorFunction(Uppercase.NAME, Uppercase.class);
        addMessageProcessorFunction(Concat.NAME, Concat.class);
        addMessageProcessorFunction(KeyValue.NAME, KeyValue.class);
        addMessageProcessorFunction(Join.NAME, Join.class);
        addMessageProcessorFunction(Split.NAME, Split.class);
        addMessageProcessorFunction(StartsWith.NAME, StartsWith.class);
        addMessageProcessorFunction(Replace.NAME, Replace.class);
        addMessageProcessorFunction(Length.NAME, Length.class);
        addMessageProcessorFunction(FirstNonNull.NAME, FirstNonNull.class);

        // json
        addMessageProcessorFunction(JsonParse.NAME, JsonParse.class);
        addMessageProcessorFunction(SelectJsonPath.NAME, SelectJsonPath.class);

        // dates
        addMessageProcessorFunction(DateConversion.NAME, DateConversion.class);
        addMessageProcessorFunction(Now.NAME, Now.class);
        addMessageProcessorFunction(ParseDate.NAME, ParseDate.class);
        addMessageProcessorFunction(ParseUnixMilliseconds.NAME, ParseUnixMilliseconds.class);
        addMessageProcessorFunction(FlexParseDate.NAME, FlexParseDate.class);
        addMessageProcessorFunction(FormatDate.NAME, FormatDate.class);
        addMessageProcessorFunction(Years.NAME, Years.class);
        addMessageProcessorFunction(Months.NAME, Months.class);
        addMessageProcessorFunction(Weeks.NAME, Weeks.class);
        addMessageProcessorFunction(Days.NAME, Days.class);
        addMessageProcessorFunction(Hours.NAME, Hours.class);
        addMessageProcessorFunction(Minutes.NAME, Minutes.class);
        addMessageProcessorFunction(Seconds.NAME, Seconds.class);
        addMessageProcessorFunction(Millis.NAME, Millis.class);
        addMessageProcessorFunction(PeriodParseFunction.NAME, PeriodParseFunction.class);

        // hash digest
        addMessageProcessorFunction(CRC32.NAME, CRC32.class);
        addMessageProcessorFunction(CRC32C.NAME, CRC32C.class);
        addMessageProcessorFunction(MD5.NAME, MD5.class);
        addMessageProcessorFunction(Murmur3_32.NAME, Murmur3_32.class);
        addMessageProcessorFunction(Murmur3_128.NAME, Murmur3_128.class);
        addMessageProcessorFunction(SHA1.NAME, SHA1.class);
        addMessageProcessorFunction(SHA256.NAME, SHA256.class);
        addMessageProcessorFunction(SHA512.NAME, SHA512.class);

        // encoding
        addMessageProcessorFunction(Base16Encode.NAME, Base16Encode.class);
        addMessageProcessorFunction(Base16Decode.NAME, Base16Decode.class);
        addMessageProcessorFunction(Base32Encode.NAME, Base32Encode.class);
        addMessageProcessorFunction(Base32Decode.NAME, Base32Decode.class);
        addMessageProcessorFunction(Base32HumanEncode.NAME, Base32HumanEncode.class);
        addMessageProcessorFunction(Base32HumanDecode.NAME, Base32HumanDecode.class);
        addMessageProcessorFunction(Base64Encode.NAME, Base64Encode.class);
        addMessageProcessorFunction(Base64Decode.NAME, Base64Decode.class);
        addMessageProcessorFunction(Base64UrlEncode.NAME, Base64UrlEncode.class);
        addMessageProcessorFunction(Base64UrlDecode.NAME, Base64UrlDecode.class);

        // ip handling
        addMessageProcessorFunction(CidrMatch.NAME, CidrMatch.class);
        addMessageProcessorFunction(IpAddressConversion.NAME, IpAddressConversion.class);

        // null support
        addMessageProcessorFunction(IsNull.NAME, IsNull.class);
        addMessageProcessorFunction(IsNotNull.NAME, IsNotNull.class);

        // URL parsing
        addMessageProcessorFunction(UrlConversion.NAME, UrlConversion.class);
        addMessageProcessorFunction(UrlDecode.NAME, UrlDecode.class);
        addMessageProcessorFunction(UrlEncode.NAME, UrlEncode.class);

        // Syslog support
        addMessageProcessorFunction(SyslogFacilityConversion.NAME, SyslogFacilityConversion.class);
        addMessageProcessorFunction(SyslogLevelConversion.NAME, SyslogLevelConversion.class);
        addMessageProcessorFunction(SyslogPriorityConversion.NAME, SyslogPriorityConversion.class);
        addMessageProcessorFunction(SyslogPriorityToStringConversion.NAME, SyslogPriorityToStringConversion.class);

        // Lookup tables
        addMessageProcessorFunction(Lookup.NAME, Lookup.class);
        addMessageProcessorFunction(LookupValue.NAME, LookupValue.class);
        addMessageProcessorFunction(LookupStringList.NAME, LookupStringList.class);
        addMessageProcessorFunction(LookupSetValue.NAME, LookupSetValue.class);
        addMessageProcessorFunction(LookupClearKey.NAME, LookupClearKey.class);
        addMessageProcessorFunction(LookupSetStringList.NAME, LookupSetStringList.class);
        addMessageProcessorFunction(LookupAddStringList.NAME, LookupAddStringList.class);
        addMessageProcessorFunction(LookupRemoveStringList.NAME, LookupRemoveStringList.class);

        // Debug
        addMessageProcessorFunction(Debug.NAME, Debug.class);
        addMessageProcessorFunction(MetricCounterIncrement.NAME, MetricCounterIncrement.class);
    }

    protected void addMessageProcessorFunction(String name, Class<? extends Function<?>> functionClass) {
        addMessageProcessorFunction(binder(), name, functionClass);
    }

    public static MapBinder<String, Function<?>> processorFunctionBinder(Binder binder) {
        return MapBinder.newMapBinder(binder, TypeLiteral.get(String.class), new TypeLiteral<Function<?>>() {});
    }

    public static void addMessageProcessorFunction(Binder binder, String name, Class<? extends Function<?>> functionClass) {
        processorFunctionBinder(binder).addBinding(name).to(functionClass);

    }
}
