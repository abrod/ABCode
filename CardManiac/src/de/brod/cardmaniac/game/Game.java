package de.brod.cardmaniac.game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import android.util.Log;
import de.brod.cardmaniac.Card;
import de.brod.cardmaniac.Hand;
import de.brod.cardmaniac.MainActivity;
import de.brod.cardmaniac.R;
import de.brod.gui.GuiButton;
import de.brod.gui.IAction;

public abstract class Game<CARD extends Card> implements IGame<CARD> {

	private List<GuiButton> lstButtons = new ArrayList<GuiButton>();
	private MainActivity _main;

	@Override
	public void init(MainActivity main, float wd, float hg, int width, int height) {
		_main = main;
		init(wd, hg, width, height);
	}

	protected abstract void init(float wd, float hg, int width, int height);

	@Override
	public List<? extends GuiButton> getButtons() {
		return lstButtons;
	}

	public void createGuiButton(float x, float y, float wdButton, float hgButton, IAction action) {
		GuiButton guiButton = new GuiButton(x, y, wdButton, hgButton, action);
		lstButtons.add(guiButton);
	}

	protected ArrayList<Hand<CARD>> hands = new ArrayList<Hand<CARD>>();

	@Override
	public List<? extends Hand<CARD>> getHands() {
		return hands;
	}

	@Override
	public List<? extends Card> actionDown(CARD card) {
		@SuppressWarnings("unchecked")
		Hand<CARD> hand = (Hand<CARD>) card.getHand();
		List<CARD> lst = new ArrayList<CARD>();
		hand.actionDown(card, lst);
		return lst;
	}

	@Override
	public void actionUp(List<? extends CARD> lstActionCards, Hand<CARD> handTo) {
		handTo.actionUp(lstActionCards);
	}

	public String getString(int id) {
		return _main.getString(id);
	}

	protected void askForNewGame() {
		_main.confirm(getString(R.string.question), getString(R.string.do_you_want_to_start_a_new_game),
				getString(R.string.confirm_yes), new IAction() {

					@Override
					public String getTitle() {
						return getString(R.string.confirm);
					}

					@Override
					public void doAction() {
						_main.newGame();
					}
				}, getString(R.string.confirm_no), null);

	}

	List<CARD> getAllCards() {
		List<CARD> lstAllCards = new ArrayList<CARD>();
		// get all cards
		for (Hand<CARD> hand : hands) {
			lstAllCards.addAll(hand.getCards());
		}
		Collections.shuffle(lstAllCards);
		return lstAllCards;
	}

	@Override
	public void loadGame() {
		File file = getDatFile();
		try {
			Hashtable<Integer, CARD> htCards = new Hashtable<Integer, CARD>();
			// get all cards
			for (Hand<CARD> hand : hands) {
				for (CARD card : hand.getCards()) {
					htCards.put(card.getUniqueId(), card);
				}
			}
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			for (Hand<CARD> hand : hands) {
				int size = in.readInt();
				for (int i = 0; i < size; i++) {
					int iCard = in.readInt();
					CARD removedCard = htCards.remove(iCard);
					if (removedCard == null) {
						// card not found
						Log.e("Error", "Card not found");
						in.close();
						resetGame();
						return;
					}
					hand.addCard(removedCard);
				}
			}
			in.close();
		} catch (IOException e) {
			// could not read
			Log.e("Error", e.getLocalizedMessage());
		}
	}

	@Override
	public void saveGame() {
		try {
			File file = getDatFile();
			Log.d(getClass().getSimpleName(), "Save " + file.getAbsolutePath());
			DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
			writeTo(out);
			out.close();
		} catch (IOException e) {
			Log.e("Error", e.getLocalizedMessage());
		}
	}

	private File getDatFile() {
		return new File(_main.getFilesDir(), getClass().getSimpleName() + ".dat");
	}

	void writeTo(DataOutputStream out) throws IOException {
		for (Hand<CARD> hand : hands) {
			List<CARD> cards = hand.getCards();
			out.writeInt(cards.size());
			for (CARD card : cards) {
				int id = card.getUniqueId();
				out.writeInt(id);
			}
		}
	}
}
