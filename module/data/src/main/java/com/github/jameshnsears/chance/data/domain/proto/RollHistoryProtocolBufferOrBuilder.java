// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: roll_history.proto

// Protobuf Java Version: 3.25.3
package com.github.jameshnsears.chance.data.domain.proto;

public interface RollHistoryProtocolBufferOrBuilder extends
        // @@protoc_insertion_point(interface_extends:com.github.jameshnsears.chance.data.domain.proto.RollHistoryProtocolBuffer)
        com.google.protobuf.MessageOrBuilder {

    /**
     * <code>map&lt;int64, .com.github.jameshnsears.chance.data.domain.proto.RollListProtocolBuffer&gt; values = 1;</code>
     */
    int getValuesCount();

    /**
     * <code>map&lt;int64, .com.github.jameshnsears.chance.data.domain.proto.RollListProtocolBuffer&gt; values = 1;</code>
     */
    boolean containsValues(
            long key);

    /**
     * Use {@link #getValuesMap()} instead.
     */
    @java.lang.Deprecated
    java.util.Map<java.lang.Long, com.github.jameshnsears.chance.data.domain.proto.RollListProtocolBuffer>
    getValues();

    /**
     * <code>map&lt;int64, .com.github.jameshnsears.chance.data.domain.proto.RollListProtocolBuffer&gt; values = 1;</code>
     */
    java.util.Map<java.lang.Long, com.github.jameshnsears.chance.data.domain.proto.RollListProtocolBuffer>
    getValuesMap();

    /**
     * <code>map&lt;int64, .com.github.jameshnsears.chance.data.domain.proto.RollListProtocolBuffer&gt; values = 1;</code>
     */
    /* nullable */
    com.github.jameshnsears.chance.data.domain.proto.RollListProtocolBuffer getValuesOrDefault(
            long key,
            /* nullable */
            com.github.jameshnsears.chance.data.domain.proto.RollListProtocolBuffer defaultValue);

    /**
     * <code>map&lt;int64, .com.github.jameshnsears.chance.data.domain.proto.RollListProtocolBuffer&gt; values = 1;</code>
     */
    com.github.jameshnsears.chance.data.domain.proto.RollListProtocolBuffer getValuesOrThrow(
            long key);
}