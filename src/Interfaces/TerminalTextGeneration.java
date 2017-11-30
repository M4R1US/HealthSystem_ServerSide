package Interfaces;

import SavedVariables.FinalConstants;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

/**
 * Created by Marius Baltramaitis on 07/10/2017.
 */
public interface TerminalTextGeneration {

    default Text generateText(String textSource, String colorHex)
    {
        //implement fonts here
        Text text = new Text(textSource + System.lineSeparator());
        text.setFill(Paint.valueOf(colorHex));
        text.setFont(FinalConstants.TERMINAL_FONT);
        return text;
    }
}
