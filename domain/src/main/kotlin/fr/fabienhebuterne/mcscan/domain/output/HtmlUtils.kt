package fr.fabienhebuterne.mcscan.domain.output

interface Element {
    fun render(builder: StringBuilder, indent: String)
}

class TextElement(val text: String) : Element {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent$text\n")
    }
}

abstract class Tag(val name: String) : Element {
    val children = arrayListOf<Element>()
    val attributes = hashMapOf<String, String>()

    protected fun <T : Element> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent<$name${renderAttributes()}>\n")
        for (c in children) {
            c.render(builder, "$indent  ")
        }
        builder.append("$indent</$name>\n")
    }

    private fun renderAttributes(): String? {
        val builder = StringBuilder()
        for (a in attributes.keys) {
            builder.append(" $a=\"${attributes[a]}\"")
        }
        return builder.toString()
    }


    override fun toString(): String {
        val builder = StringBuilder()
        render(builder, "")
        return builder.toString()
    }
}

abstract class TagWithText(name: String) : Tag(name) {
    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }
}

fun div(className: String? = null, init: DIV.() -> Unit): DIV {
    val div = DIV()
    div.init()
    className?.let { div.className = className }
    return div
}

class DIV() : TagWithText("div") {
    var className: String
        get() = attributes["class"]!!
        set(value) {
            attributes["class"] = value
        }

    fun b(init: B.() -> Unit) = initTag(B(), init)
    fun span(style: String? = null, init: SPAN.() -> Unit) {
        val a = initTag(SPAN(), init)
        style?.let { a.style = style }
    }
    fun div(className: String? = null, init: DIV.() -> Unit): DIV {
        val div = initTag(DIV(), init)
        className?.let { div.className = className }
        return div
    }
}

class B() : TagWithText("b")
class A() : TagWithText("a") {
    public var href: String
        get() = attributes["href"]!!
        set(value) {
            attributes["href"] = value
        }
}

class SPAN() : TagWithText("span") {
    public var style: String
        get() = attributes["style"]!!
        set(value) {
            attributes["style"] = value
        }
}
