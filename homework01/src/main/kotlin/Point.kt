
interface Point: DimentionAware

/**
 * Реализация Point по умолчаению
 *
 * Должны работать вызовы DefaultPoint(10), DefaultPoint(12, 3), DefaultPoint(12, 3, 12, 4, 56)
 * с любым количество параметров
 *
 * Сама коллекция параметров недоступна, доступ - через методы интерфейса
 */
class DefaultPoint(private vararg val vector: Int): Point {
    override val ndim = this.vector.size

    override fun dim(i: Int): Int {
        if (i >= ndim) {
            throw DimensionException.NoSuchDimension("Point doesn't have $i dimension")
        }
        return vector[i]
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DefaultPoint

        if (!vector.contentEquals(other.vector)) return false

        return true
    }

    override fun hashCode(): Int {
        return vector.contentHashCode()
    }

}