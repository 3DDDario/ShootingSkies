package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

class Laser {

    //position and dimensions
    Rectangle laserBoundingBox;

    //physical characteristics
    float movementSpeed; //world units per second

    TextureRegion textureRegion;

    public Laser(float xPosition, float yPosition,
                 float width, float height, float movementSpeed,
                 TextureRegion textureRegion) {
        this.laserBoundingBox = new Rectangle(xPosition, yPosition, width, height);
        this.movementSpeed = movementSpeed;
        this.textureRegion = textureRegion;
    }

    public void draw(Batch batch) {
        batch.draw(textureRegion, laserBoundingBox.x, laserBoundingBox.y,
                laserBoundingBox.width, laserBoundingBox.height);
    }

 //   public Rectangle getLaserBoundingBox() {
   //     return laserBoundingBox;
    //}
}
