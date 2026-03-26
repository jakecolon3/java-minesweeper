import java.awt.*;
import java.awt.event.*;

public class AWTExample extends Frame { // a gui implementation has to extend java.awt.Frame

    /* basics of awt
     *
     *    there are 2 groups of gui elements: components and containers
     *    - components are what actually adds functionality to the gui
     *      common component types are Button, Label, TextField etc.
     *      Label is simply a string of text positioned inside a container
     *    - containers hold components in a specific *layout*
     *      a gui program typically contains a single top-level container
     *      usually these are either `Frame`, `Dialog` or `Applet`
     *      Dialog is secondary to Frame, which is the actual main window of the program
     *      Applet is basically deprecated
    */

    // creating a label
    public static void label_example() {
        var myLabel = new Label("this is a label");
        String myText = myLabel.getText();
        myLabel.setText(myText + ", it contains text");
    }


    /* example of a simple gui application */

    // put these in fields so other components can interact with them (see ExampleListener)
    private int counter = 0;
    private TextField field;

    private class ExampleListener implements ActionListener { // creating a listener class for the button
        // we need to override the original implementation (?)
        @Override
        public void actionPerformed(ActionEvent event) {
            // this will just increment counter when the button is pressed
            counter++;
            // updates the TextField's text
            field.setText(counter + "");
        }
    }

    // this code HAS to be inside of the constructor of a class that extends Frame or it complains
    public AWTExample() {
        setTitle("Counter example");
        setLayout(new FlowLayout()); // 'flow' means left -> right
        setSize(200, 100);


        var label = new Label("Counter: ");
        field = new TextField(counter + "", 5); // the int is the number of columns that the field should display
        var button = new Button("Add");

        field.setEditable(false);

        button.addActionListener(new ExampleListener()); // the part it complains about

        // since this extends Frame it is a container and can add components to itself
        add(label);
        add(field);
        add(button);

        System.out.println(this);
        setVisible(true);
        System.out.println(this);
    }


    public static void main(String[] args) {
        new AWTExample();
    }

}
