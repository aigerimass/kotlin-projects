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
    override fun plus(other: BinomialHeap<T>): BinomialHeap<T> = BinomialHeap(mergeTrees(trees, other.trees))

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
    fun top(): T {
        val top = trees.filterNotNull().minOfOrNull { it.value }
        if (top == null) throw IllegalArgumentException()
        else return top
    }

    /*
     * удаление элемента
     *
     * Требуемая сложность - O(log(n))
     */
    fun drop(): BinomialHeap<T> {
        val minTree = minTree()
        val leftTrees = trees.map {
            if (it?.value != minTree.value && it?.order != minTree.order) it
            else null
        }
        return BinomialHeap(mergeTrees(leftTrees, minTree.children.map { it }))
    }

    private fun minTree() : BinomialTree<T> {
        val top = top()
        return trees.first { it?.value == top }!!
    }

    private fun mergeTrees(left: FList<BinomialTree<T>?>, right: FList<BinomialTree<T>?>): FList<BinomialTree<T>?> {
        if (left.size < right.size) return mergeTrees(right, left)
        return recursiveMerge(left.reverse(), right.reverse(), null).reverse()
    }

    private fun recursiveMerge(left: FList<BinomialTree<T>?>, right: FList<BinomialTree<T>?>, addTree: BinomialTree<T>?
    ): FList<BinomialTree<T>?> {
        if (left.isEmpty) {
            return if (addTree == null) FList.Nil() else FList.Cons(addTree, FList.Nil())
        }
        left as FList.Cons

        if (right.isEmpty)
            return if (addTree == null) left
            else if (left.head == null) FList.Cons(addTree, left.tail)
            else FList.Cons(null, recursiveMerge(left.tail, FList.Nil(), addTree + left.head))
        right as FList.Cons

        return if (left.head == null && right.head == null)
            FList.Cons(addTree, recursiveMerge(left.tail, right.tail, null))
        else if (left.head != null && right.head != null)
            FList.Cons(addTree, recursiveMerge(left.tail, right.tail, left.head + right.head))
        else if (addTree == null)
            FList.Cons(left.head ?: right.head, recursiveMerge(left.tail, right.tail, null))
        else FList.Cons(null, recursiveMerge(left.tail, right.tail, addTree + (left.head ?: right.head)!!))
    }
}
