package Listeners;

/**
 * <h2>Created by Marius Baltramaitis on 02/06/2017</h2>
 * <p>
 *     Abstract class for custom text listeners with general methods mainly for text validation purposes.
 * </p>
 */
public abstract class AbstractTextAnalyzer {

    /**
     * Iteration throw text
     * @param text Text to read
     * @param legalInputCharacters String with all possible legal letters/Symbols
     * @return false if text contains characters that are not inside legalInputCharacters, true otherwise
     */
    protected boolean containsIllegalCharacters(String text, String legalInputCharacters)
    {
        for(char x : text.toCharArray())
        {
            if(!legalInputCharacters.contains(x+""))
                return true;
        }

        return false;
    }


}
