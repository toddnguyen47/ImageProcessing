/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imgpro;

import javax.swing.JTextField;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 *
 * @author ToddNguyen
 */
public class FocusTextField extends JTextField {

	public FocusTextField() {
		addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				FocusTextField.this.select(0, getText().length());
			}

			@Override
			public void focusLost(FocusEvent e) {
				FocusTextField.this.select(0, 0);
			}
		});
	}
}
