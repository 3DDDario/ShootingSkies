package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;

public class GameScreen implements Screen {

    //screen
    private Camera camera;
    private Viewport viewport;

    //graphics
    private SpriteBatch batch;
    private TextureRegion[] backgrounds;
    private TextureAtlas textureAtlas;
    private Texture explosionTexture;
    private float backgroundWidth = 1440;

    private TextureRegion
            playerShipTextureRegion, playerShieldTextureRegion,
            enemyShipTextureRegion, enemyShieldTextureRegion,
            playerLaserTextureRegion, enemyLaserTextureRegion;

    //timing
    private float backgroundSpeed = 200;
    private float background1XPos;
    private float background2XPos;
    private int backgroundIndex1 = 0;
    private int backgroundIndex2 = 1;
    private float timeBetweenEnemySpawns = 3f;
    private float enemySpawnTimer = 0;

    //world parameters
    private final float WORLD_WIDTH = 1440;
    private final float WORLD_HEIGHT = 720;
    private final float TOUCH_MOVEMENT_THRESHOLD = 10f;

    //game objects
    private PlayerShip playerShip;
    private LinkedList<EnemyShip> enemyShipLinkedList;
    private LinkedList<Laser> playerLaserLinkedList;
    private LinkedList<Laser> enemyLaserLinkedList;
    private LinkedList<Explosion> explosionLinkedList;

    private int score = 0;

    //Heads-Up Display (HUD)
    BitmapFont font;
    float hudVerticalMargin, hudLeftX, hudRightX, hudCentreX, hudRow1Y,
            hudRow2Y, hudSectionWidth;


    GameScreen() {
         camera = new OrthographicCamera();
         viewport = new StretchViewport(
                 WORLD_WIDTH, WORLD_HEIGHT, camera);

        textureAtlas = new TextureAtlas("00images.atlas");

        backgrounds = new TextureRegion[4];
        //initialize background
        for (int i = 0; i < 4; i++) {
            backgrounds[i] = new TextureRegion(textureAtlas.findRegion("background" + i));
        }

        background1XPos = 0;
        background2XPos = -1440;
        backgroundWidth = WORLD_WIDTH;

        //initialize texture regions
        playerShipTextureRegion = textureAtlas.findRegion("playerShip2_blue");
        enemyShipTextureRegion = textureAtlas.findRegion("enemyRed3");
        playerShieldTextureRegion = textureAtlas.findRegion("shield2");
        enemyShieldTextureRegion = textureAtlas.findRegion("shield1");

        playerLaserTextureRegion= textureAtlas.findRegion("laserBlue03");
        enemyLaserTextureRegion= textureAtlas.findRegion("laserRed03");

        explosionTexture = new Texture("explosion.png");

        playerShip = new PlayerShip((float)WORLD_WIDTH/5,
                (float)WORLD_HEIGHT/2,100, 100,
                300,7, 30, 8,
                550, 0.5f,
                playerShipTextureRegion, playerShieldTextureRegion,
                playerLaserTextureRegion);

        enemyShipLinkedList = new LinkedList<>();

        playerLaserLinkedList = new LinkedList<>();
        enemyLaserLinkedList = new LinkedList<>();

        explosionLinkedList = new LinkedList<>();

        batch = new SpriteBatch();

        prepareHUD();
    }

    private void prepareHUD() {
        //Create a BitmapFont from font file
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(
                Gdx.files.internal("EdgeOfTheGalaxyRegular.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParameter.size = 72;
        fontParameter.borderWidth= 3.6f;
        fontParameter.color = new Color(255,130,20, 0.85f);
        fontParameter.borderColor = new Color(230, 252, 244, 0.55f);

        font = fontGenerator.generateFont(fontParameter);

        //scale the size of the font
        font.getData().setScale(0.8f);

        //calculate hud margins
        hudVerticalMargin = font.getCapHeight() / 2;
        hudLeftX = hudVerticalMargin;
        hudRightX = WORLD_WIDTH * 2/3 - hudLeftX;
        hudCentreX = WORLD_WIDTH / 3;
        hudRow1Y = WORLD_HEIGHT - hudVerticalMargin;
        hudRow2Y = WORLD_HEIGHT - hudVerticalMargin - font.getCapHeight()*5/4;
        hudSectionWidth = WORLD_WIDTH / 3;
    }

    @Override
    public void render(float deltaTime) {
        batch.begin();

        //sliding background
        renderBackground(deltaTime);

        detectInput(deltaTime);
        playerShip.update(deltaTime);

        spawnEnemyShips(deltaTime);

        ListIterator<EnemyShip> enemyShipListIterator = enemyShipLinkedList.listIterator();
        while (enemyShipListIterator.hasNext()) {
            EnemyShip enemyShip = enemyShipListIterator.next();
            moveEnemy(enemyShip, deltaTime);
            enemyShip.update(deltaTime);
            enemyShip.draw(batch);
        }
        //player ship
        playerShip.draw(batch);

        //lasers
        renderLasers(deltaTime);

        detectCollisions();

        //detect laser/ship collisions
        updateAndRenderExplosions(deltaTime);

        //hud rendering
        updateAndRenderHUD();

        batch.end();
    }

    private void updateAndRenderHUD() {
        //render top row
        font.draw(batch, "Score", hudLeftX, hudRow1Y, hudSectionWidth,
                Align.left, false);
        font.draw(batch, "Shield", hudCentreX, hudRow1Y, hudSectionWidth,
                Align.center, false);
        font.draw(batch, "Lives", hudRightX, hudRow1Y, hudSectionWidth,
                Align.center, false);
        //render second row
        font.draw(batch,
                String.format(Locale.getDefault(), "%06d", score),
                hudLeftX , hudRow2Y, hudSectionWidth, Align.left, false);
        font.draw(batch,
                String.format(Locale.getDefault(), "%02d", playerShip.shield),
                hudCentreX , hudRow2Y, hudSectionWidth, Align.center, false);
        font.draw(batch,
                String.format(Locale.getDefault(), "%02d", playerShip.lives),
                hudRightX , hudRow2Y, hudSectionWidth, Align.center, false);
    }

    private void spawnEnemyShips(float deltaTime) {
        enemySpawnTimer += deltaTime;
        if(enemySpawnTimer > timeBetweenEnemySpawns) {
            enemyShipLinkedList.add(new EnemyShip
                    (Math.max((float) WORLD_WIDTH / 2,
                            MyGdxGame.random.nextFloat() * WORLD_WIDTH - 100),
                            Math.max(100, MyGdxGame.random.nextFloat() * WORLD_HEIGHT - 100),
                            100, 100,
                            220, 1, 25, 6,
                            500, 0.8f,
                            enemyShipTextureRegion, enemyShieldTextureRegion,
                            enemyLaserTextureRegion));
            enemySpawnTimer -= timeBetweenEnemySpawns;
        }
    }

    //touch input (also mouse)
    private void detectInput(float deltaTime) {
        float leftLimit, rightLimit, upLimit, downLimit;
        leftLimit = -playerShip.shipBoundingBox.x;
        downLimit = -playerShip.shipBoundingBox.y;
        rightLimit = (float)WORLD_WIDTH*3/4 - playerShip.shipBoundingBox.x - playerShip.shipBoundingBox.width;
        upLimit = WORLD_HEIGHT - playerShip.shipBoundingBox.y - playerShip.shipBoundingBox.height;


        if (Gdx.input.isTouched()) {
            //get the screen position of the touch
            float xTouchPixels = Gdx.input.getX();
            float yTouchPixels = Gdx.input.getY();

            //convert to world position
            Vector2 touchPoint = new Vector2(xTouchPixels, yTouchPixels);
            touchPoint = viewport.unproject(touchPoint);

            //calculate the x and y differences
            Vector2 playerShipCentre = new Vector2(
                    playerShip.shipBoundingBox.x + playerShip.shipBoundingBox.width/2,
                    playerShip.shipBoundingBox.y + playerShip.shipBoundingBox.height/2);

            float touchDistance = touchPoint.dst(playerShipCentre);

            if (touchDistance > TOUCH_MOVEMENT_THRESHOLD) {
                //we set the value and the sign of the x and y, the last depending on if we
                // touched at the right/up or at the left/bottom of our ship
                float xTouchDifference = touchPoint.x - playerShipCentre.x;
                float yTouchDifference = touchPoint.y - playerShipCentre.y;

                //we divide by touchDistance in order to don't have the speed of the ship
                // to increase by the distance of the touch from the ship
                float xMove = (xTouchDifference * playerShip.movementSpeed * deltaTime) / touchDistance;
                float yMove = (yTouchDifference * playerShip.movementSpeed * deltaTime) / touchDistance;

                if (xMove > 0) xMove = Math.min(xMove, rightLimit);
                else xMove = Math.max(xMove,leftLimit);

                if (yMove > 0) yMove = Math.min(yMove, upLimit);
                else yMove = Math.max(yMove,downLimit);

                playerShip.translate(xMove,yMove);
            }
        }

    }

    private void moveEnemy(EnemyShip enemyShip, float deltaTime){
        float leftLimit, rightLimit, upLimit, downLimit;
        leftLimit = WORLD_WIDTH/2-enemyShip.shipBoundingBox.x;
        downLimit = -enemyShip.shipBoundingBox.y;
        rightLimit = WORLD_WIDTH - enemyShip.shipBoundingBox.x - enemyShip.shipBoundingBox.width;
        upLimit = WORLD_HEIGHT - enemyShip.shipBoundingBox.y - enemyShip.shipBoundingBox.height;;

        //scale to the maximum speed of the ship
        float xMove = enemyShip.getDirectionVector().x * enemyShip.movementSpeed * deltaTime;
        float yMove = enemyShip.getDirectionVector().y * enemyShip.movementSpeed * deltaTime;

        if (xMove > 0) xMove = Math.min(xMove, rightLimit);
        else xMove = Math.max(xMove,leftLimit);

        if (yMove > 0) yMove = Math.min(yMove, upLimit);
        else yMove = Math.max(yMove,downLimit);

        enemyShip.translate(xMove,yMove);
    }

    private void detectCollisions(){
        //check intersect between laser and enemy ship
        ListIterator<Laser> laserListIterator = playerLaserLinkedList.listIterator();
        while(laserListIterator.hasNext()) {

            Laser laser = laserListIterator.next();

            ListIterator<EnemyShip> enemyShipListIterator =
                    enemyShipLinkedList.listIterator();

            while (enemyShipListIterator.hasNext()) {

                EnemyShip enemyShip = enemyShipListIterator.next();

                if (enemyShip.intersects(laser.laserBoundingBox)) {
                    if(enemyShip.hitAndCheckIfDestroyed(laser)) {
                        enemyShipListIterator.remove();
                        explosionLinkedList.add(
                                new Explosion(explosionTexture,
                                new Rectangle(enemyShip.shipBoundingBox),
                                0.7f));
                        score += 100;
                    }
                    laserListIterator.remove();
                    //the break is because if a laser his a ship
                    // it's impossible that it hits an other one
                    break;
                }
            }
        }

        //check intersect between laser and player ship
        laserListIterator = enemyLaserLinkedList.listIterator();
        while(laserListIterator.hasNext()) {
            Laser laser = laserListIterator.next();
            if (playerShip.intersects(laser.laserBoundingBox)) {
                if(playerShip.hitAndCheckIfDestroyed(laser)){
                    explosionLinkedList.add(new Explosion(explosionTexture,
                             new Rectangle(playerShip.shipBoundingBox),
                            1.6f));
                playerShip.lives--;
                }
                laserListIterator.remove();
            }
        }
    }

    private void updateAndRenderExplosions(float deltaTime) {
        ListIterator<Explosion> explosionListIterator = explosionLinkedList.listIterator();
        while (explosionListIterator.hasNext()) {
            Explosion explosion = explosionListIterator.next();
            explosion.update(deltaTime);
            if (explosion.isFinished()) {
                explosionListIterator.remove();
            } else {
                explosion.draw(batch);
            }
        }

    }

    private void renderLasers(float deltaTime) {
        //create new lasers
        //player lasers
        if (playerShip.canFireLaser()) {
            Laser[] lasers = playerShip.fireLasers();
            playerLaserLinkedList.addAll(Arrays.asList(lasers));
        }
        //enemy lasers
        ListIterator<EnemyShip> enemyShipListIterator = enemyShipLinkedList.listIterator();
        while (enemyShipListIterator.hasNext()) {
            EnemyShip enemyShip = enemyShipListIterator.next();
            if (enemyShip.canFireLaser()) {
                Laser[] lasers = enemyShip.fireLasers();
                enemyLaserLinkedList.addAll(Arrays.asList(lasers));
            }
        }
        //draw lasers
        //remove old lasers
        ListIterator<Laser> iterator = playerLaserLinkedList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.laserBoundingBox.x += laser.movementSpeed * deltaTime;
            if (laser.laserBoundingBox.x > WORLD_WIDTH) {
                iterator.remove();
            }
        }
        iterator = enemyLaserLinkedList.listIterator();
        while (iterator.hasNext()) {
            Laser laser = iterator.next();
            laser.draw(batch);
            laser.laserBoundingBox.x -= laser.movementSpeed * deltaTime;
            if (laser.laserBoundingBox.x + laser.laserBoundingBox.height < 0) {
                iterator.remove();
            }
        }
    }

    private void renderBackground(float deltaTime){
        background1XPos += backgroundSpeed * deltaTime;
        background2XPos += backgroundSpeed * deltaTime;


        if (background1XPos > WORLD_WIDTH) {
            background1XPos = -1440;
            if (backgroundIndex1 == 0) backgroundIndex1 += 2;
            else if (backgroundIndex1 == 2) backgroundIndex1 -= 2;
        } else if (background2XPos > WORLD_WIDTH) {
            background2XPos = -1440;
            if (backgroundIndex2 == 1) backgroundIndex2 += 2;
            else if (backgroundIndex2 == 3) backgroundIndex2 -= 2;
        }

        batch.draw(backgrounds[backgroundIndex1], background1XPos , 0);
        batch.draw(backgrounds[backgroundIndex2], background2XPos , 0);

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);

    }

    @Override
    public void show() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
