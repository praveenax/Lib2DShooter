package com.mygdx.game;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.TimeUtils;

public class MyGdxGame extends ApplicationAdapter implements EventListener {
	SpriteBatch batch;
	Texture img;
	Texture back;
	Texture fireButton;
	Texture jumpButton;
	Texture ground;

	Stage stage;

	protected OrthographicCamera camera;

	float elapsedTime;
	Button b;

	TextureRegion[] animationFrames;
	Animation animation;
	TextureRegion[][] tmpFrames;
	Texture allheros;

	float heroX;
	float heroY;

	boolean bulletState = false;
	Texture bullet;
	private boolean firstRenderBullet;
	private Sprite bulletSprite;
	private float bulletTime = 0;

	private boolean flip = true;

	TextureRegion firstHero;
	private Music music;
	private Animation spiningVillianAnimate;

	Stack<Sprite> gBulletStack;
	Map<Sprite,Integer> bulletTimerMap = new HashMap<Sprite,Integer>();

	@Override
	public void create() {
		gBulletStack = new Stack<Sprite>();
		batch = new SpriteBatch();
		stage = new Stage();
		bullet = new Texture("bullet.png");
		img = new Texture("hero1.png");
		back = new Texture("apo.jpg");

		ground = new Texture("groundTiled.png");

		fireButton = new Texture("fireButton.png");
		jumpButton = new Texture("jumpButton.png");

		camera = new OrthographicCamera(50, 25);
		// renderer = new Box2DDebugRenderer();
		// world = new World(new Vector2(0, -10), true);

		// BodyDef bodyDef = new BodyDef();
		// groundBody = world.createBody(bodyDef);
		// createWorld(world);

		Skin skin = new Skin();

		defineSkin(skin);

		TextButtonStyle textButtonStyle = defineTextButtonStyle(skin);
		// b = new TextButton("Test", textButtonStyle);
		// b = new ImageButton(skin);
		// b.setPosition(10, 10);
		ImageButtonStyle style = imageButtonFire(textButtonStyle);

		ImageButtonStyle style2 = imageButtonJump(textButtonStyle);

		ImageButton b = new ImageButton(style);
		b.getImageCell().expandX().fillX();

		ImageButton b2 = new ImageButton(style2);
		b2.getImageCell().expandX().fillX();
		b.setX(50);
		b.setY(50);
		// b.setWidth(200);
		// b.setHeight(100);
		b2.setX(Gdx.graphics.getWidth() - 200);
		b2.setY(50);

		b.addListener(this);
		b2.addListener(this);

		b.setName("fireButton");
		b2.setName("jumpButton");

		stage.addActor(b);
		stage.addActor(b2);

		Gdx.input.setInputProcessor(stage);

		// for animation
		allheros = new Texture("heroSheet3.png");
		tmpFrames = TextureRegion.split(allheros, 45, 64);
		animationFrames = new TextureRegion[10];

		firstHero = tmpFrames[0][0];

		int index = 0;
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 1; j++) {
				animationFrames[index++] = tmpFrames[j][i];
			}
		}
		heroX = 20;
		heroY = 173;

		animation = new Animation(1f, animationFrames);

		TextureRegion[] villianAnimFrames = new TextureRegion[3];
		Texture spinningBlade = new Texture("blade.png");

		int bladeIndex = 0;
		TextureRegion[][] tmpFrames2 = TextureRegion.split(spinningBlade, 55,
				44);
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 1; j++) {
				villianAnimFrames[bladeIndex++] = tmpFrames2[j][i];
			}
		}

		spiningVillianAnimate = new Animation(0.5f, villianAnimFrames);

		// play music

		music = Gdx.audio.newMusic(Gdx.files.internal("music/loop.mp3"));
		// music.play();
		// music.setLooping(true);

	}

	private ImageButtonStyle imageButtonJump(TextButtonStyle textButtonStyle) {
		ImageButtonStyle style2 = new ImageButtonStyle(textButtonStyle);
		TextureRegion tr2 = new TextureRegion();
		tr2.setRegion(jumpButton);
		style2.imageUp = new TextureRegionDrawable(tr2);
		style2.imageDown = new TextureRegionDrawable(tr2);
		return style2;
	}

	private ImageButtonStyle imageButtonFire(TextButtonStyle textButtonStyle) {
		ImageButtonStyle style = new ImageButtonStyle(textButtonStyle);
		TextureRegion tr = new TextureRegion();
		tr.setRegion(fireButton);
		style.imageUp = new TextureRegionDrawable(tr);
		style.imageDown = new TextureRegionDrawable(tr);
		return style;
	}

	private TextButtonStyle defineTextButtonStyle(Skin skin) {
		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = skin.newDrawable("white", Color.valueOf("2E97BA"));
		textButtonStyle.down = skin.newDrawable("white",
				Color.valueOf("2E97BA"));
		textButtonStyle.checked = skin.newDrawable("white",
				Color.valueOf("2E97BA"));
		textButtonStyle.over = skin.newDrawable("white",
				Color.valueOf("2E97BA"));
		textButtonStyle.font = skin.getFont("default");
		return textButtonStyle;
	}

	private void defineSkin(Skin skin) {
		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		skin.add("white", new Texture(pixmap));

		// Store the default libgdx font under the name "default".
		skin.add("default", new BitmapFont());
	}

	int gndCount = 0;
	private boolean jumpAnim = false;

	int jumpCount = 0;
	private boolean fireAction;

	@Override
	public void render() {
		clearScreen();

		// setting input listener for buttons
		Gdx.input.setInputProcessor(stage);

		batch.begin();
		// adding the space background
		batch.draw(back, 0, 0);

		TextureRegion tTemp = new TextureRegion(ground);
		tTemp.setRegion(0, 0, 32, 32);

		renderGround(tTemp);

		// flip is used for ground annimation switch after 60frames
		if (gndCount % 60 == 0) {
			flip = !flip;

		}
		gndCount++;
		// batch.draw(img, 150, 200, 80, 80);
		// batch.draw(animation.getKeyFrame(elapsedTime, true), heroX
		// + (elapsedTime * 5), heroY, 80, 80);

		heroRender();

		renderGBullet();

		renderBBullet();

		renderVillians();

		// bullet render

		batch.end();

		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		stage.draw();

		elapsedTime = elapsedTime + 0.1f;
		bulletTime = bulletTime + 1;
	}

	// bad bullet
	private void renderBBullet() {
		// TODO Auto-generated method stub

	}

	// good bullet
	private void renderGBullet() {

		// check if any bullet is live
		if (!gBulletStack.isEmpty()) {
			// draw the bullet

			Iterator<Sprite> itr = gBulletStack.iterator();
			while (itr.hasNext()) {

				Sprite tmp = itr.next();
				float bX = tmp.getX();
				float bY = tmp.getY();

				
				
				Integer bulletDelta = bulletTimerMap.get(tmp);
				bX = bX + (elapsedTime - bulletDelta) * 30;
				
				
				batch.draw(tmp, bX, bY, 30, 10);

				if (bX > Gdx.graphics.getWidth()) {
//					gBulletStack.remove(tmp);
					itr.remove();
					bulletTimerMap.remove(tmp);
				}

			}

		} else {

		}

	}

	private void renderVillians() {
		// villians will get spawned from the end of the screen.
		// they move towards the hero
		// they will try to collide with the hero
		// if - they are hit by a bullet - bullet vanishes - villian vanishes
		// if - villian collides with hero - hero vanishes - game over
		// when many villians are spawned add to the list of villians on screen
		//
		float hX = Gdx.graphics.getWidth();
		float hY = Gdx.graphics.getHeight();

		float vX = hX - 100 - elapsedTime * 10;

		// move the villian towards the hero
		// check for contact of hero position and the villian
		if (heroX + 45 > vX) {
			// System.out.println("Out");

		} else {
			batch.draw(spiningVillianAnimate.getKeyFrame(elapsedTime, true), hX
					- 100 - elapsedTime * 10, heroY, 80, 80);
		}

		// call spawn function when a villian is destroyed

	}

	private void clearScreen() {
		Gdx.gl.glClearColor(1, 1, 1, 0.5f);//
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);//
	}

	private void heroRender() {
		if (jumpAnim) {

			jumpAction();

		} else {

			batch.draw(animation.getKeyFrame(elapsedTime, true), heroX, heroY,
					80, 80);
		}

		if (fireAction) {
			// start bullet render
			Sprite bulletSpr = new Sprite(bullet);
			bulletSpr.setX(heroX + 60);
			bulletSpr.setY(heroY + 60);
			fireAction = false;
			bulletTimerMap.put(bulletSpr, Math.round(elapsedTime));
			gBulletStack.push(bulletSpr);
		}
	}

	private void renderGround(TextureRegion tTemp) {
		for (int it = 0; it < 30; it++) {
			if (flip) {
				batch.draw(tTemp, (it * 64) - 64, 128, 0, 1, 16, 16, 4, 4, 0);
				batch.draw(tTemp, (it * 64) - 64, 64, 0, 1, 16, 16, 4, 4, 0);
				// batch.draw(tTemp, (it * 64) - 64, 0, 0, 1, 16, 16, 4, 4, 0);

			} else {
				batch.draw(tTemp, (it * 64) - 32, 128, 0, 1, 16, 16, 4, 4, 0);
				batch.draw(tTemp, (it * 64) - 32, 64, 0, 1, 16, 16, 4, 4, 0);
				// batch.draw(tTemp, (it * 64) - 32, 0, 0, 1, 16, 16, 4, 4, 0);

			}

		}
	}

	private void jumpAction() {
		jumpCount++;

		if (jumpCount < 5) {
			batch.draw(firstHero, heroX, heroY + 30, 80, 80);
		} else if (jumpCount > 5 && jumpCount < 10) {
			batch.draw(firstHero, heroX, heroY + 75, 80, 80);
		} else if (jumpCount > 10 && jumpCount < 15) {
			batch.draw(firstHero, heroX, heroY + 30, 80, 80);

		} else if (jumpCount == 15) {
			batch.draw(firstHero, heroX, heroY + 10, 80, 80);
			jumpAnim = false;
			jumpCount = 0;
		}
	}

	@Override
	public boolean handle(Event event) {
		// TODO Auto-generated method stub
		// System.out.println(event.getTarget());

		String eventOrginName = "";
		eventOrginName = event.getTarget().getName();

		if (eventOrginName != null && eventOrginName.equals("fireButton")) {
			// System.out.println("fire");
			fireBullet();

		} else if (eventOrginName != null
				&& eventOrginName.equals("jumpButton")) {
			// System.out.println("jump");
			jumpHero();
		}

		return false;
	}

	private void jumpHero() {
		// TODO Auto-generated method stub
		jumpAnim = true;

	}

	private void fireBullet() {
		// TODO Auto-generated method stub

		// get x & y of hX+5,hY
		// bulletState = true;
		// firstRenderBullet = true;

		fireAction = true;

	}

	// things to do
	// the hero should keep moving forward
	// on tap on left button - jump
	// on tap on right button - fire gun

	// move the hero with simple animation
	// create random villians and road blocks
	// add life points
	// add coins & heart to collect
	// kill 10 villians to win
	// speed of stage changes the level difficulty

}
