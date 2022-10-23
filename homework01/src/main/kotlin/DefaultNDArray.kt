interface NDArray : SizeAware, DimentionAware {
    /*
     * Получаем значение по индексу point
     *
     * Если размерность point не равна размерности NDArray
     * бросаем IllegalPointDimensionException
     *
     * Если позиция по любой из размерностей некорректна с точки зрения
     * размерности NDArray, бросаем IllegalPointCoordinateException
     */
    fun at(point: Point): Int

    /*
     * Устанавливаем значение по индексу point
     *
     * Если размерность point не равна размерности NDArray
     * бросаем IllegalPointDimensionException
     *
     * Если позиция по любой из размерностей некорректна с точки зрения
     * размерности NDArray, бросаем IllegalPointCoordinateException
     */
    fun set(point: Point, value: Int)

    /*
     * Копируем текущий NDArray
     *
     */
    fun copy(): NDArray

    /*
     * Создаем view для текущего NDArray
     *
     * Ожидается, что будет создан новая реализация интерфейса.
     * Но она не должна быть видна в коде, использующем эту библиотеку как внешний артефакт
     *
     * Должна быть возможность делать view над view.
     *
     * In-place-изменения над view любого порядка видна в оригинале и во всех view
     *
     * Проблемы thread-safety игнорируем
     */
    fun view(): NDArray

    /*
     * In-place сложение
     *
     * Размерность other либо идентична текущей, либо на 1 меньше
     * Если она на 1 меньше, то по всем позициям, кроме "лишней", она должна совпадать
     *
     * Если размерности совпадают, то делаем поэлементное сложение
     *
     * Если размерность other на 1 меньше, то для каждой позиции последней размерности мы
     * делаем поэлементное сложение
     *
     * Например, если размерность this - (10, 3), а размерность other - (10), то мы для три раза прибавим
     * other к каждому срезу последней размерности
     *
     * Аналогично, если размерность this - (10, 3, 5), а размерность other - (10, 5), то мы для пять раз прибавим
     * other к каждому срезу последней размерности
     */
    fun add(other: NDArray)

    /*
     * Умножение матриц. Immutable-операция. Возвращаем NDArray
     *
     * Требования к размерности - как для умножения матриц.
     *
     * this - обязательно двумерна
     *
     * other - может быть двумерной, с подходящей размерностью, равной 1 или просто вектором
     *
     * Возвращаем новую матрицу (NDArray размерности 2)
     *
     */
    fun dot(other: NDArray): NDArray
}

/*
 * Базовая реализация NDArray
 *
 * Конструкторы должны быть недоступны клиенту
 *
 * Инициализация - через factory-методы ones(shape: Shape), zeros(shape: Shape) и метод copy
 */
class DefaultNDArray private constructor(
    private val shape: DefaultShape, private val values: IntArray
) : NDArray {
    companion object {
        fun zeros(shape: DefaultShape): NDArray {
            return DefaultNDArray(shape, IntArray(shape.size) { 0 })
        }

        fun ones(shape: DefaultShape): NDArray {
            return DefaultNDArray(shape, IntArray(shape.size) { 1 })
        }
    }

    override fun at(point: Point): Int = values[getIndexByPoint(point)]

    override fun set(point: Point, value: Int) {
        values[getIndexByPoint(point)] = value
    }

    override fun copy(): NDArray = DefaultNDArray(shape, values.clone())

    override fun view(): NDArray = DefaultNDArray(shape, values)

    override fun add(other: NDArray) {
        when (ndim) {
            other.ndim -> {
                (0 until size).forEach { values[it] += other.at(getPointByIndex(it, ndim)) }
            }
            1 -> {
                (0 until size).forEach { values[it] += other.at(getPointByIndex(it, ndim - 1)) }
            }
            else -> throw NDArrayException.IllegalNDArrayDimsException(ndim, other.ndim)
        }
    }

    override fun dot(other: NDArray): NDArray {
        if (ndim != 2) {
            throw NDArrayException.IllegalDotArgumentsException("Dims $ndim cannot be != 2")
        } else if (other.ndim > 2) {
            throw NDArrayException.IllegalDotArgumentsException("Other dims (${other.ndim}) cannot be > 2")
        } else if (other.dim(0) != dim(1)) {
            throw NDArrayException.IllegalDotArgumentsException("dimensions not compatible")
        }
        val newShape = DefaultShape(dim(0), if (other.ndim == 2) other.dim(1) else 1)
        val newValues = IntArray(newShape.dim(0) * newShape.dim(1)) { 0 }
        var index = 0
        for (i in (0 until newShape.dim(0))) {
            for (j in 0 until newShape.dim(1)) {
                for (k in 0 until dim(1)) {
                    newValues[index] += this.at(DefaultPoint(i, k)) * other.at(
                        if (other.ndim == 2) DefaultPoint(
                            k, j
                        ) else DefaultPoint(k)
                    )
                }
                index++
            }
        }
        return DefaultNDArray(newShape, newValues)
    }

    private fun getIndexByPoint(point: Point): Int {
        checkPoint(point)
        return (0 until point.ndim).sumOf { point.dim(it) * posByDim[it] }
    }

    private fun getPointByIndex(ind: Int, pointDims: Int): Point {
        val point = IntArray(pointDims)
        var index = ind
        (0 until pointDims).forEach {
            point[it] = index / posByDim[it]
            index %= posByDim[it]
        }
        return DefaultPoint(*point)
    }

    private fun checkPoint(point: Point) {
        if (ndim != point.ndim) {
            throw NDArrayException.IllegalPointDimensionException(point.ndim, shape.ndim)
        }
        for (d in 0 until shape.ndim) {
            if (point.dim(d) < 0 || shape.dim(d) <= point.dim(d)) {
                throw NDArrayException.IllegalPointCoordinateException(d, point.dim(d), shape.dim(d))
            }
        }
    }

    override val ndim: Int
        get() = shape.ndim

    override val size: Int
        get() = shape.size

    private val posByDim = IntArray(shape.ndim)

    init {
        posByDim[shape.ndim - 1] = 1
        for (i in shape.ndim - 1 downTo 1) {
            posByDim[i - 1] = posByDim[i] * shape.dim(i)
        }
    }

    override fun dim(i: Int): Int {
        if (i >= ndim) {
            throw DimensionException.NoSuchDimension("Shape doesn't have $i dimension")
        }
        return values[i]
    }
}

sealed class NDArrayException(reason: String = "") : Exception(reason) {
    class IllegalPointCoordinateException(index: Int, pointDim: Int, shapeDim: Int) : NDArrayException(
        "Trouble at $index : value is $pointDim, shape dimension is $shapeDim"
    )

    class IllegalPointDimensionException(pointDims: Int, shapeDims: Int) : NDArrayException(
        "Shape has $shapeDims dims, point has $pointDims, should be equal"
    )

    class IllegalAddArgumentsException(index: Int, expected: Int, actual: Int) : NDArrayException(
        "$index position: $expected in current NDArray not equals to other's $actual"
    )

    class IllegalNDArrayDimsException(NDim: Int, otherNDim: Int) : NDArrayException(
        "dims incompatible: other: $otherNDim, this: $NDim"
    )

    class IllegalDotArgumentsException(reason: String) : NDArrayException(reason)
}