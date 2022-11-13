package binomial

/*
 * BinomialHeap - реализация биномиальной кучи
 *
 * https://en.wikipedia.org/wiki/Binomial_heap
 *
 * Запрещено использовать
 *
 *  - var
 *  - циклы
 *  - стандартные коллекции
 *
 * Детали внутренней реазации должны быть спрятаны
 * Создание - только через single() и plus()
 *
 * Куча совсем без элементов не предусмотрена
 *
 * Операции
 *
 * plus с кучей
 * plus с элементом
 * top - взятие минимального элемента
 * drop - удаление минимального элемента
 */
class BinomialHeap<T: Comparable<T>> private constructor(private val trees: FList<BinomialTree<T>?>): SelfMergeable<BinomialHeap<T>> {
    companion object {
        fun <T: Comparable<T>> single(value: T): BinomialHeap<T> = BinomialHeap(flistOf(BinomialTree.single(value)))
    }

    /*
     * слияние куч
     *
     * Требуемая сложность - O(log(n))
     */
    override fun plus(other :BinomialHeap<T>): BinomialHeap<T> {
        if (trees.isEmpty) return other
        if (other.trees.isEmpty) return other
        val treesCons = trees as FList.Cons
        val oTreesCons = other.trees as FList.Cons
        if (treesCons.head!!.order == oTreesCons.head!!.order)
            return BinomialHeap(FList.Cons(oTreesCons.head.plus(treesCons.head),
                BinomialHeap(oTreesCons.tail).plus(BinomialHeap(treesCons.tail)).trees))
        if (treesCons.head.order < oTreesCons.head.order) {
            return BinomialHeap(FList.Cons(treesCons.head, FList.Cons(oTreesCons.head,
                BinomialHeap(oTreesCons.tail).plus(BinomialHeap(treesCons.tail)).trees)))
        }
        return BinomialHeap(FList.Cons(oTreesCons.head, FList.Cons(treesCons.head,
                BinomialHeap(oTreesCons.tail).plus(BinomialHeap(treesCons.tail)).trees)))
    }

    /*
     * добавление элемента
     * 
     * Требуемая сложность - O(log(n))
     */
    operator fun plus(elem: T): BinomialHeap<T> = plus(single(elem))

    /*
     * минимальный элемент
     *
     * Требуемая сложность - O(log(n))
     */
    fun top(): T = if (trees.isEmpty) throw NoSuchElementException() else trees.minOf { tree -> tree!!.value }

    /*
     * удаление элемента
     *
     * Требуемая сложность - O(log(n))
     */
    fun drop(): BinomialHeap<T> {
        TODO()
    }
}

