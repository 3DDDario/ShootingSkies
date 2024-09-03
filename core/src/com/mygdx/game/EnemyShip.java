package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

class EnemyShip extends Ship {

    Vector2 directionVector;
    float timeSinceLastDirectionChange = 0;
    float directionChangeFrequency = 0.75f;

    public EnemyShip(float xPosition, float yPosition,
                      float width, float height, float movementSpeed,
                      int shield, float laserWidth, float laserHeight,
                      float laserMovementSpeed, float timeBetweenShoots,
                      TextureRegion shipTextureRegion,
                      TextureRegion shieldTextureRegion,
                      TextureRegion laserTextureRegion) {
        super(xPosition, yPosition, width, height, movementSpeed,
                shield, laserWidth, laserHeight, laserMovementSpeed,
                timeBetweenShoots, shipTextureRegion,
                shieldTextureRegion, laserTextureRegion);
        directionVector = new Vector2(0, -1);
    }

    public Vector2 getDirectionVector() {
        return directionVector;
    }

    private void randomizeDirectionVector() {
        double bearing = MyGdxGame.random.nextDouble()*6.283185;
        //result = 0 to 2 * PI

        directionVector.x = (float) Math.sin(bearing);
        directionVector.y = (float) Math.cos(bearing);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        timeSinceLastDirectionChange += deltaTime;
        if (timeSinceLastDirectionChange > directionChangeFrequency) {
            randomizeDirectionVector();
            timeSinceLastDirectionChange -= directionChangeFrequency;
        }
    }

    @Override
    public Laser[] fireLasers() {

        Laser[] laser = new Laser[2];
        laser[0] = new Laser(shipBoundingBox.x - shipBoundingBox.width*1.15f,
                shipBoundingBox.y + shipBoundingBox.height*0.80f, laserWidth,
                laserHeight, laserMovementSpeed*1.50f, laserTextureRegion);

        laser[1] = new Laser(shipBoundingBox.x - shipBoundingBox.width*1.50f,
                shipBoundingBox.y + shipBoundingBox.height*0.20f, laserWidth,
                laserHeight, laserMovementSpeed*1.50f, laserTextureRegion);

        //timeSinceLastShot -= timeBetweenShots could improve app speed
        timeSinceLastShot = 0;

        return laser;
    }

    public void draw(Batch batch) {
        batch.draw(shipTextureRegion, shipBoundingBox.x, shipBoundingBox.y,
                shipBoundingBox.width, shipBoundingBox.height);
        if (shield > 0) {
            batch.draw(shieldTextureRegion, (shipBoundingBox.x - 25),
                    shipBoundingBox.y, shipBoundingBox.width, shipBoundingBox.height);
        }
    }
}
