
interface Shape: DimentionAware, SizeAware

/**
 * Реализация Point по умолчаению
 *
 * Должны работать вызовы DefaultShape(10), DefaultShape(12, 3), DefaultShape(12, 3, 12, 4, 56)
 * с любым количество параметров
 *
 * При попытке создать пустой Shape бросается EmptyShapeException
 *
 * При попытке указать неположительное число по любой размерности бросается NonPositiveDimensionException
 * Свойство index - минимальный индекс с некорректным значением, value - само значение
 *
 * Сама коллекция параметров недоступна, доступ - через методы интерфейса
 */
class DefaultShape(private vararg val dimensions: Int): Shape {
    override val ndim = dimensions.size
    override fun dim(i: Int): Int = dimensions[i]

    override val size: Int

    init {
        if (dimensions.isEmpty())
            throw ShapeArgumentException.EmptyShapeException()

        var volume = 1
        dimensions.forEachIndexed { i, it ->
            if (it <= 0)
                throw ShapeArgumentException.NonPositiveDimensionException(i, it)
            volume *= it
        }
        this.size = volume
    }
}

sealed class ShapeArgumentException (reason: String = "") : IllegalArgumentException(reason) {
    // EmptyShapeException
    class EmptyShapeException() : ShapeArgumentException("Empty Shape")
    // NonPositiveDimensionException(val index: Int, val value: Int)
    class NonPositiveDimensionException(
        index : Int,
        value : Int) :
        ShapeArgumentException("Non positive dimension in index $index : $value")
}
