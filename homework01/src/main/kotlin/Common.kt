
interface DimentionAware {
    val ndim: Int // сколько измерений
    fun dim(i: Int): Int
}

sealed class DimensionException (reason: String = "") : IllegalArgumentException(reason) {
    class NoSuchDimension(reason: String) : DimensionException(reason)
}

interface SizeAware {
    val size: Int
}