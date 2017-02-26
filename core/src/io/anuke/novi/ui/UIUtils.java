package io.anuke.novi.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class UIUtils{
	public static void addCloseButton (VisDialog dialog) {
		Label titleLabel = dialog.getTitleLabel();
		Table titleTable =  dialog.getTitleTable();

		VisTextButton closeButton = new VisTextButton("X", "blue");
		titleTable.add(closeButton).padRight(- dialog.getPadRight() + 1).padTop(- 1).size(50);
		closeButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				 dialog.hide();
			}
		});
		closeButton.addListener(new ClickListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				event.cancel();
				return true;
			}
		});

		if (titleLabel.getLabelAlign() == Align.center && titleTable.getChildren().size == 2)
			titleTable.getCell(titleLabel).padLeft(closeButton.getWidth() * 2);
	}
}
