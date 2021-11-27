package org.propig.game.platformer;


public enum FaceDirection{
    Left(-1),
    Front(0),
    Right(1);

    public int value;
    FaceDirection(int value){
        this.value = value;
    }
    public static FaceDirection getDirection(int value){
        if(value == -1){
            return FaceDirection.Left;
        } else if(value == 0) {
            return FaceDirection.Front;
        } else {
            return FaceDirection.Right;
        }
    }
}