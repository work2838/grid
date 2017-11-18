

package com.mygdx.game;
/*
*
*
* Libgdx stratagey game by Garrett Workman
*
* Credit for pokemon sprite pack to Petie on spriters-resource
*
*
*/

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;


public class grid extends ApplicationAdapter implements InputProcessor{

	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	private ShapeRenderer sr;
	private SpriteBatch sb;
	private Vector2 mouse;
	int renPosX=10;
	int renPosY=10;
	int occupied = 0;
	Array <unit> P1,P2;
	int turn;
	int tmpx,tmpy;
	Music playMusic;
	Sound attack;
/*	@Override
	public void create () {
		setScreen(new Play());
	}
*/
	@Override
	public void create () {

		//pulls the map and sets the proper size
		TmxMapLoader loader = new TmxMapLoader();
		map = loader.load("map2.tmx");
		renderer = new OrthogonalTiledMapRenderer(map,1/16f);
		camera = new OrthographicCamera();
		camera.setToOrtho(false,30,20);
		sr = new ShapeRenderer();
		mouse = new Vector2();
		sb = new SpriteBatch();
		Gdx.input.setInputProcessor(this);
		turn = 0;
		set_units();
		playMusic = Gdx.audio.newMusic(Gdx.files.internal("happy.wav"));
		playMusic.setVolume(.25f);
		playMusic.setLooping(true);
		playMusic.play();
		attack = Gdx.audio.newSound(Gdx.files.internal("beep.wav"));
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.5f,0.5f,0.5f,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		renderer.setView(camera);
		renderer.render();
		sr.setProjectionMatrix(camera.combined);
		sb.setProjectionMatrix(camera.combined);

		sr.begin(ShapeRenderer.ShapeType.Line);
		sr.setColor(0,0,0,1);
		for (int i=0; i<20; i++){
			sr.line((float)i,0, (float)i,20);
		}
		for (int i=0; i<20; i++){
			sr.line(0,(float)i, 20,(float)i);
		}
		sr.end();

		Gdx.gl.glEnable(GL20.GL_BLEND);
		sr.begin(ShapeRenderer.ShapeType.Filled);
		sr.setColor(0,0,1,.4f);
		if(occupied ==1) {
			sr.rect(renPosX - 1, renPosY - 1, 3, 3);
			sr.rect(renPosX - 2, renPosY, 1, 1);
			sr.rect(renPosX, renPosY - 2, 1, 1);
			sr.rect(renPosX + 2, renPosY, 1, 1);
			sr.rect(renPosX, renPosY + 2, 1, 1);
		}
		sr.end();

		Gdx.gl.glDisable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);

		sb.begin();
		for(unit Unit : P1){
			Unit.render(sb);
		}
		for(unit Unit : P2){
			Unit.render(sb);
		}
		sb.end();

		Gdx.gl.glEnable(GL20.GL_BLEND);
		sr.begin(ShapeRenderer.ShapeType.Filled);
		sr.setColor(1,0,0,.6f);
		for(unit Unit : P1){
			sr.rect(Unit.x_loc, Unit.y_loc, 1, 1);
		}
		sr.setColor(1,2,0,.6f);
		for(unit Unit : P2){
			sr.rect(Unit.x_loc, Unit.y_loc, 1, 1);
		}


		sr.end();

		Gdx.gl.glDisable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA,GL20.GL_ONE_MINUS_SRC_ALPHA);
	}

	void set_units(){
		P1= new Array<unit>();
		P2= new Array<unit>();
		for(int i=0; i < 10; i++){
			unit Unit = new unit();
			Unit.init(19,i+5,1);
			P1.add(Unit);
		}
		for(int i=0; i < 10; i++){
			unit Unit = new unit();
			Unit.init(0,i+5,1);
			P2.add(Unit);
		}
	}

	void refresh_units(){
		if(turn==0) {
			for (unit Unit : P1) {
				Unit.state = 1;
				if (Unit.health == 0)
					P1.removeValue(Unit, false);
			}
		}
		if(turn==1) {
			for (unit Unit : P2) {
				Unit.state = 1;
				if (Unit.health == 0)
					P2.removeValue(Unit, false);
			}
		}
	}

	@Override
	public void dispose () {
		map.dispose();
		renderer.dispose();
	}

	@Override
	public void resize(int width, int height){
		camera.viewportWidth=30;
		camera.viewportHeight=20;
		camera.update();
	}

	public boolean is_legal(unit tmp, int x, int y, int tn){
		if((x<=tmp.x_loc+2&&x>=tmp.x_loc-2)&&y==tmp.y_loc && x<20){
			return true;
		}
		else if((y<=tmp.y_loc+2&&y>=tmp.y_loc-2)&&x==tmp.x_loc && x<20){
			return true;
		}
		else if((x==tmp.x_loc+1 || x==tmp.x_loc-1)&&(y==tmp.y_loc+1 || y==tmp.y_loc-1)&& x<20){
			return true;
		}
		else return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Vector3 position = camera.unproject(new Vector3(screenX,screenY,0));

		renPosX=(int)position.x;
		renPosY=(int)position.y;
		System.out.print((int)position.x +"  "+(int)position.y+" turn = "+turn+" \n");

		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT) &&(renPosX>21 && renPosX<28)&&(renPosY>3 && renPosY<7)){
			System.out.print("you're here turn = "+turn+" \n");
			refresh_units();
			if(turn == 1)
				turn = 0;
			else if(turn == 0)
				turn = 1;
		}
		else if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)&&occupied==0){
			if(turn==1) {
				for (unit Unit : P1) {
					if (renPosX == Unit.x_loc && renPosY == Unit.y_loc&&Unit.state==1) {
						occupied = 1;
						tmpx=renPosX;
						tmpy=renPosY;
					}
				}
			}

			if(turn==0) {
				for (unit Unit : P2) {
					if (renPosX == Unit.x_loc && renPosY == Unit.y_loc&&Unit.state==1) {
						occupied = 1;
						tmpx=renPosX;
						tmpy=renPosY;
					}
				}
			}
		}
		else if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)&&occupied==1){
			if(turn==1) {
				for (unit Unit : P1) {
					if (tmpx == Unit.x_loc && tmpy == Unit.y_loc && is_legal(Unit,renPosX,renPosY,turn)) {
						Unit.x_loc=renPosX;
						Unit.y_loc=renPosY;
						Unit.state=0;
						for(unit Unit2 : P2){
							if (Unit2.x_loc==renPosX && Unit2.y_loc==renPosY){
								Unit2.health=0;
								attack.play();
							}
						}
						occupied = 0;
					}
					else occupied=0;
				}
			}

			if(turn==0) {
				for (unit Unit : P2) {
					if (tmpx == Unit.x_loc && tmpy == Unit.y_loc && is_legal(Unit,renPosX,renPosY,turn)) {
						Unit.x_loc=renPosX;
						Unit.y_loc=renPosY;
						Unit.state=0;
						for(unit Unit2 : P1){
							if (Unit2.x_loc==renPosX && Unit2.y_loc==renPosY){
								Unit2.health=0;
								attack.play();
							}

						}
						occupied = 0;
					}
					else occupied=0;
				}
			}

			refresh_units();
		}
		else
			occupied = 0;




		return true;
	}


	@Override
	public void pause(){

	}

	@Override
	public void resume(){

	}


	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}


	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
