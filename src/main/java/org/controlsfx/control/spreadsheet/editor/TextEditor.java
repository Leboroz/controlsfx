/**
 * Copyright (c) 2013, ControlsFX
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of ControlsFX, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.controlsfx.control.spreadsheet.editor;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.controlsfx.control.spreadsheet.control.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.model.DataCell;


/**
 *
 * Specialization of the {@link Editor} Class.
 * It displays a {@link TextField} where the user can type a different value.
 */
public class TextEditor extends Editor {

	/***************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/
	private final TextField tf;

	/***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/
	public TextEditor() {
		super();
		tf = new TextField();
		tf.setPrefHeight(20);
	}

	/***************************************************************************
     *                                                                         *
     * Public Methods                                                          *
     *                                                                         *
     **************************************************************************/
	@Override
	public void startEdit() {
		super.startEdit();
		tf.setMaxHeight(20);
		attachEnterEscapeEventHandler();

		// If the SpreadsheetCell is deselected, we commit.
		// Sometimes, when you you touch the scrollBar when editing, this is called way
		// too late and the SpreadsheetCell is null, so we need to be careful.
		il = new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {

				if (spreadsheetCell != null && spreadsheetCell.isEditing()) {
					commitEdit();
					spreadsheetCell.commitEdit(cell);
				}
				end();


			}
		};

		spreadsheetCell.selectedProperty().addListener(il);
		spreadsheetCell.setGraphic(tf);
		
		
		 
		final Runnable r = new Runnable() {
			@Override
			public void run() {
				tf.requestFocus();
			}
		};
		Platform.runLater(r);
	}
	
	/***************************************************************************
     *                                                                         *
     * Protected Methods                                                       *
     *                                                                         *
     **************************************************************************/
	@Override
	protected void begin(DataCell<?> cell, SpreadsheetCell bc) {
		this.cell = cell;
		this.spreadsheetCell = bc;
		tf.setText(cell.getStr());

	}

	@Override
	protected void end() {
		super.end();
		if(spreadsheetCell != null) {
			spreadsheetCell.selectedProperty().removeListener(il);
		}

		tf.setOnKeyPressed(null);
		this.cell = null;
		this.spreadsheetCell = null;
		il = null;
	}

	@Override
	protected DataCell<?> commitEdit() {
		this.cell.setStr(tf.getText());
		return cell;
	}

	@Override
	protected void cancelEdit() {
		end();
	}

	@Override
	protected Control getControl() {
		return tf;
	}

	@Override
	protected void attachEnterEscapeEventHandler() {
		tf.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent t) {
				if (t.getCode() == KeyCode.ENTER) {
					commitEdit();
					spreadsheetCell.commitEdit(cell);
					end();
				} else if (t.getCode() == KeyCode.ESCAPE) {
					spreadsheetCell.cancelEdit();
					cancelEdit();
				}
			}
		});
	}
}