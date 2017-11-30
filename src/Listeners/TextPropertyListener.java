package Listeners;

import SavedVariables.FinalConstants;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.control.TextField;


/**
 * <h2>Created by Marius Baltramaitis on 05-Feb-17</h2>
 * <p>Listener for text input depending of input control object type</p>
 */
public class TextPropertyListener implements ChangeListener<String> {

    private TextInputControl textInputObject;
    public String formattedString = "";


    /**
     * Constructor taking necessary parameters
     * @param textInputObject textInputObject where text should be taken from
     */
    public TextPropertyListener(TextInputControl textInputObject)
    {
        this.textInputObject = textInputObject;
    }

    /**
     *  Overriding method to validate input
     * @param observable observable itself
     * @param oldValue old String value
     * @param newValue current String value
     */
    @Override
    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
    {
        if(textInputObject instanceof TextField)
        {
            validateTextField((TextField)textInputObject,oldValue,newValue);
        }

        if(textInputObject instanceof TextArea)
        {
            validateTextArea((TextArea)textInputObject,oldValue,newValue);
        }
    }

    /**
     * Special Method for validating input for text fields
     * @param textField textField where text should be taken from
     * @param oldValue old String value
     * @param newValue current String value
     */
    private void validateTextField(TextField textField, String oldValue,String newValue)
    {
        if(newValue.isEmpty())
        {
            textField.setText("");
            return;
        }

        for(char x : newValue.toCharArray())
        {
            if(!FinalConstants.allValidTextLetters.contains(x+""))
            {
                textField.setText(oldValue);
                return;
            }
        }

        formattedString = newValue.substring(0,1).toUpperCase() + newValue.substring(1).toLowerCase();
        textField.setText(formattedString);

    }


    /**
     * Special Method for validating input for text areas
     * @param textArea textArea where text should be taken from
     * @param oldValue old String value
     * @param newValue current String value
     */
    private void validateTextArea(TextArea textArea,String oldValue, String newValue)
    {
        if(newValue.isEmpty())
        {
            textArea.setText("");
            return;
        }

        for(char x : newValue.toCharArray())
        {
            if(!FinalConstants.allValidTextAreaLetters.contains(x+""))
            {
                textArea.setText(oldValue);
                return;
            }
        }

        formattedString = newValue.substring(0,1).toUpperCase() + newValue.substring(1);
        textArea.setText(formattedString);
    }


}
