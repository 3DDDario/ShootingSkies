package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

class PlayerShip extends Ship {

    int lives;
    public PlayerShip(float xPosition, float yPosition,
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
        lives = 3;
    }

    @Override
    public Laser[] fireLasers() {

        Laser[] laser = new Laser[2];
        //for position w add 15% to the x value of the Ship
        //and set the position of y in the middle
        laser[0] = new Laser(shipBoundingBox.x + shipBoundingBox.width*1.15f,
                shipBoundingBox.y + shipBoundingBox.height*0.10f, laserWidth,
                laserHeight, laserMovementSpeed, laserTextureRegion);

        laser[1] = new Laser(shipBoundingBox.x + shipBoundingBox.width*1.50f,
                shipBoundingBox.y + shipBoundingBox.height*0.70f, laserWidth,
                laserHeight, laserMovementSpeed*1.40f, laserTextureRegion);

    //timeSinceLastShot -= timeBetweenShots could improve app speed
        timeSinceLastShot = 0;

        return laser;
    }

}
