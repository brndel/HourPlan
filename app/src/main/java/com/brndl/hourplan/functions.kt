package com.brndl.hourplan

fun select(boolean: Boolean, trueValue: Any?, falseValue: Any?): Any? {
    return if(boolean) trueValue else falseValue
}