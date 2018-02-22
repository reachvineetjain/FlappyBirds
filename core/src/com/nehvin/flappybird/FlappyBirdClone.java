package com.nehvin.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBirdClone extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture backgroundImage;
	private Texture[] birds;
	private Texture pipe1;
	private Texture pipe2;
	private Texture gameOver;
//	private ShapeRenderer shapeRenderer ;

	private int flapState=0;
	private float birdY;
	private int gameState = 0;
	private float velocity = 0;
	private float gravity = 2;
	private float gap = 400;
//	private float maxTubeOffset;
	private Random randomGenerator;
	private float tubeVelocity = 4;
	private int numberOfTubes = 4;
	private float[] tubeX = new float[numberOfTubes];
	private float[] tubeOffset = new float[numberOfTubes];
	private float distanceBetweenTubes;
	private Circle birdCircle;
	private Rectangle[] topTubeRectangle;
	private Rectangle[] bottomTubeRectangle;
	private int score=0;
	private int scoringTube = 0;
	private BitmapFont font;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		backgroundImage = new Texture("bg.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		pipe1 = new Texture("toptube.png");
		pipe2 = new Texture("bottomtube.png");
		gameOver = new Texture("gameover.png");
//		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap/2 -100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() * 3/4;
		birdCircle = new Circle();
//		shapeRenderer = new ShapeRenderer();
		topTubeRectangle = new Rectangle[numberOfTubes];
		bottomTubeRectangle = new Rectangle[numberOfTubes];
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		gameStart();

	}

	private void gameStart() {
		birdY = Gdx.graphics.getHeight()/2 - birds[flapState].getHeight()/2;
		for (int i = 0; i < numberOfTubes; i++) {
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
			tubeX[i] = Gdx.graphics.getWidth()/2 - pipe1.getWidth()/2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;
			topTubeRectangle[i] = new Rectangle();
			bottomTubeRectangle[i] = new Rectangle();
		}
	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(backgroundImage, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 1) {

			if(tubeX[scoringTube] < Gdx.graphics.getWidth()/2)
			{
				score++;
				Gdx.app.log("Score", String.valueOf(score));
				if(scoringTube < numberOfTubes -1)
					scoringTube++;
				else
					scoringTube=0;
			}

			if(Gdx.input.justTouched())
			{
				velocity = -30;
			}

			for (int i = 0; i < numberOfTubes; i++) {

				if( tubeX[i] < -pipe1.getWidth()) {
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
				}
				else
					tubeX[i] = tubeX[i] - tubeVelocity;

				batch.draw(pipe1, tubeX[i] , Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i]);
				batch.draw(pipe2, tubeX[i], Gdx.graphics.getHeight()/2 - gap/2 - pipe2.getHeight() + tubeOffset[i]);

				topTubeRectangle[i] = new Rectangle(tubeX[i] , Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i], pipe1.getWidth(),pipe1.getHeight());
				bottomTubeRectangle[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight()/2 - gap/2 - pipe2.getHeight() + tubeOffset[i], pipe2.getWidth(),pipe2.getHeight());
			}


			if (birdY > 0 ) {
				velocity = velocity + gravity;
				birdY -= velocity;
			}
			else
			{
				gameState =2;
			}
		}
		else{
			if(gameState == 0) {
				if (Gdx.input.justTouched())
					gameState = 1;
			}
			else
			{
				if(gameState == 2)
				{
					batch.draw(gameOver, Gdx.graphics.getWidth()/2 - gameOver.getWidth()/2,
							Gdx.graphics.getHeight()/2 - gameOver.getHeight()/2);
					if (Gdx.input.justTouched()) {
						gameState = 1;
						gameStart();
						score = 0;
						scoringTube = 0;
						velocity = 0;
					}
				}
			}
		}

		if(flapState==0){
			flapState = 1;
		}
		else{
			flapState = 0;
		}
		batch.draw(birds[flapState],
				Gdx.graphics.getWidth()/2 - birds[flapState].getWidth()/2,
				birdY);
		font.draw(batch, String.valueOf(score),100,100);
		birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapState].getHeight()/2,
				birds[flapState].getWidth()/2);
//		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//		shapeRenderer.setColor(Color.RED);
//		shapeRenderer.circle(birdCircle.x,birdCircle.y, birdCircle.radius);

		for (int i = 0; i < numberOfTubes; i++) {
//			shapeRenderer.rect(tubeX[i] , Gdx.graphics.getHeight()/2 + gap/2 + tubeOffset[i], pipe1.getWidth(),pipe1.getHeight());
//			shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight()/2 - gap/2 - pipe2.getHeight() + tubeOffset[i], pipe2.getWidth(),pipe2.getHeight());

			if(Intersector.overlaps(birdCircle, topTubeRectangle[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangle[i]))
			{
//				Gdx.app.log("Collision", "Yes ");
				gameState = 2;
			}

		}

//		shapeRenderer.end();
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		backgroundImage.dispose();
	}
}