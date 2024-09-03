package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.math.Rectangle;

public class Explosion {

    private Animation<TextureRegion> explosionAnimation;
    private float explosionTimer;
    private Rectangle explosionBoundingBox;

    Explosion(Texture texture, Rectangle explosionBoundingBox,
              float totalAnimationTime) {
        this.explosionBoundingBox = explosionBoundingBox;


        //split texture
        TextureRegion[][] textureRegion2D = TextureRegion.split(
                texture, 64, 64); //every single png explosion is 64x64 pixels

        //convert to 1 dimensional array
        TextureRegion[] textureRegion1D = new TextureRegion[16];
        int index = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                textureRegion1D[index] = textureRegion2D[i][j];
                index++;
            }
        }
        explosionAnimation = new Animation<TextureRegion>
                (totalAnimationTime/16, textureRegion1D);
        explosionTimer = 0;
    }

    public void update(float deltaTime) {
        explosionTimer += deltaTime;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(explosionAnimation.getKeyFrame(explosionTimer),
                explosionBoundingBox.x, explosionBoundingBox.y,
                explosionBoundingBox.width, explosionBoundingBox.height);
    }

    public boolean isFinished() {
        return explosionAnimation.isAnimationFinished(explosionTimer);
    }
}
