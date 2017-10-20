package com.jetbrains.rider.util.string

enum class Eol(val value: String) {
    asIs (""),
    linux ("\n"),
    windows ("\r\n"),
    osSpecified (System.lineSeparator())
}

class PrettyPrinter() {
    var step = 2

    var eol = Eol.asIs
    private val builder = StringBuilder()
    private var needIndent = true
    var indent: Int = 0

    fun indent(action: PrettyPrinter.() -> Unit) : PrettyPrinter {
        try {
            indent += step
            action()
        } finally {
            indent -= step
        }
        return this
    }

    fun print(str: String) {
        for (c in str) {
            if (needIndent) {
                (1..indent).forEach { builder.append(' ') }
                needIndent = false
            }

            builder.append(c)
            if (c == '\n') needIndent = true
        }
    }

    fun p(str: String) = print(str)

    val lastLine : CharSequence get() =
        if (needIndent) ""
        else builder.slice(builder.lastIndexOf('\n')+1 .. builder.lastIndex)


    fun pad(size: Int) {
        val padCount = Math.max(size - lastLine.length, 0)
        p("".padEnd(padCount))
    }

    fun println() {
        print(System.lineSeparator())
    }
    fun println(str: String) {
        print(str)
        print(System.lineSeparator())
    }

    operator fun String.unaryPlus() {
        if (!this.isEmpty()) println(this)
    }

    fun <T> Iterable<T>.println(transform: (T) -> String) {
        val iter = this.iterator()
        while (iter.hasNext()) println(transform(iter.next()))
    }

    fun <T> Iterable<T>.printlnWithBlankLine(transform: (T) -> String) {
        val iter = this.iterator()
        if (!iter.hasNext()) return

        while (iter.hasNext()) println(transform(iter.next()))
        println()
    }

    fun <T> Iterable<T>.printlnWithPrefixSuffixAndIndent(prefix: String, suffix: String, transform: (T) -> String = {it.toString()}) {
        val iter = this.iterator()
        if (!iter.hasNext()) return

        println(prefix)
        indent {
            while (iter.hasNext()) println(transform(iter.next()))
        }
        println(suffix)
    }

    fun <T> Iterable<T>.joinWithPrefixSuffixAndIndent(separator: String, prefix: String, suffix: String, transform: (T) -> String = {it.toString()}) {
        if (this.none()) return

        println(prefix)
        indent {
            + joinToString(separator) { transform(it) }
        }
        println(suffix)
    }

    override fun toString(): String {
        if (eol == Eol.asIs) return builder.toString()
        val res = StringBuilder(builder.length)

        var i = 0
        while (i < builder.length) {
            if (builder[i] == '\r' && i+1 < builder.length && builder[i+1] == '\n') {
                res.append(eol.value)
                i++
            } else if (builder[i] == '\r' || builder[i] == '\n') {
                res.append(eol.value)
            } else {
                res.append(builder[i])
            }
            i++
        }
        return res.toString()
    }

}


