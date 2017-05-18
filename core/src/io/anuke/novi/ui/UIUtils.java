package io.anuke.novi.ui;

import com.badlogic.gdx.utils.Align;

import io.anuke.ucore.scene.ui.Dialog;
import io.anuke.ucore.scene.ui.Label;
import io.anuke.ucore.scene.ui.TextButton;
import io.anuke.ucore.scene.ui.layout.Table;

public class UIUtils{
	public static void addCloseButton (Dialog dialog) {
		Label titleLabel = dialog.getTitleLabel();
		Table titleTable =  dialog.getTitleTable();

		TextButton closeButton = new TextButton("X", "blue");
		titleTable.add(closeButton).padRight(- dialog.getPadRight() + 1).padTop(- 1).size(50);
		
		closeButton.changed(()->{
			dialog.hide();
		});

		if (titleLabel.getLabelAlign() == Align.center && titleTable.getChildren().size == 2)
			titleTable.getCell(titleLabel).padLeft(closeButton.getWidth() * 2);
	}
}
