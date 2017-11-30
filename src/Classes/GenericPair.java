package Classes;

import com.sun.istack.internal.NotNull;

/**
 * <h2>Created by Marius Baltramaitis on 07-Mar-17.</h2>
 * <p>
 *     This class is just simple holder of two type Objects that are somehow connected to each other <br>
 * </p>
 */
public class GenericPair<T1, T2> {

    private T1 first;
    private T2 second;


    /**
     * Constructor taking two object
     * @param first first type object
     * @param second second type object
     */
    public GenericPair(@NotNull T1 first, @NotNull T2 second)
    {

        this.first = first;
        this.second = second;
    }


    /**
     * Getter for first type object
     * @return T1
     */
    public T1 getFirst() { return first; }

    /**
     * Getter for second type object
     * @return T2
     */
    public T2 getSecond()
    {
        return second;
    }
}
