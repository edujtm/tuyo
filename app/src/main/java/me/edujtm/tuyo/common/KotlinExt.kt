package me.edujtm.tuyo.common

import java.io.PrintWriter
import java.io.StringWriter


/** Gets the string representation of a stacktrace */
val Throwable.readableStackTrace: String
    get() {
        val writer = StringWriter()
        val printWriter = PrintWriter(writer)
        this.printStackTrace(printWriter)
        return writer.toString()
    }
