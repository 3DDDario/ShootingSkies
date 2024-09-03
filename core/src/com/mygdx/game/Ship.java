package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

abstract class Ship {

    //position & dimension
    Rectangle shipBoundingBox;

    //ship characteristics
    float movementSpeed; //world units per second
    int shield;

    //laser characteristics
    float laserWidth, laserHeight;
    float laserMovementSpeed;
    float timeBetweenShoots;
    float timeSinceLastShot = 0;

    //graphics
    TextureRegion shipTextureRegion, shieldTextureRegion, laserTextureRegion;

    public Ship(float xPosition, float yPosition, float width,
                float height, float movementSpeed, int shield,
                float laserWidth, float laserHeight,
                float laserMovementSpeed, float timeBetweenShoots,
                TextureRegion shipTextureRegion,
                TextureRegion shieldTextureRegion,
                TextureRegion laserTextureRegion) {
        this.shipBoundingBox = new Rectangle(xPosition, yPosition, width, height);
        this.movementSpeed = movementSpeed;
        this.shield = shield;
        this.laserWidth = laserWidth;
        this.laserHeight = laserHeight;
        this.laserMovementSpeed = laserMovementSpeed;
        this.timeBetweenShoots = timeBetweenShoots;
        this.shipTextureRegion = shipTextureRegion;
        this.shieldTextureRegion = shieldTextureRegion;
        this.laserTextureRegion = laserTextureRegion;
    }

    public void update(float deltaTime){
        timeSinceLastShot += deltaTime;
    }

    public boolean canFireLaser(){
        return (timeSinceLastShot - timeBetweenShoots >= 0);
    }

    public abstract Laser[] fireLasers();

    public boolean intersects(Rectangle rectangle){
        return shipBoundingBox.overlaps(rectangle);
    }

    public boolean hitAndCheckIfDestroyed(Laser laser) {
        if (shield > 0) {
            shield--;
            return false;
        }
        return true;
    }

    public void translate(float xChange, float yChange) {
        shipBoundingBox.setPosition(shipBoundingBox.x + xChange,
                shipBoundingBox.y + yChange);
    }

    public void draw(Batch batch) {
        batch.draw(shipTextureRegion, shipBoundingBox.x, shipBoundingBox.y,
                shipBoundingBox.width, shipBoundingBox.height);
        if (shield > 0) {
            batch.draw(shieldTextureRegion, shipBoundingBox.x, shipBoundingBox.y,
                    shipBoundingBox.width, shipBoundingBox.height);
        }
    }
}
