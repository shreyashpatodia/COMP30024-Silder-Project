package com.teammaxine.board.scorers;

import com.teammaxine.board.elements.*;

/**
 * Scorer that used to used in Monte Carlo for additional weighting
 * Created by noxm on 17/04/17.
 */
public class MonteCarloScorer extends Scorer {
    public MonteCarloScorer() {

    }

    public MonteCarloScorer(double dist, double count, double block, double side, double forward) {
        distance_score = dist;
        count_score = count;
        block_score = block;
        move_side_score = side;
        move_forward_score = forward;
    }

    @Override
    public double scoreBoard(Board board, char playerPiece) {
        boolean playerIsHorizontal = playerPiece == 'H';

        if(playerIsHorizontal) {
            return scoreBoardHorizontal(board);
        } else {
            return scoreBoardVertical(board);
        }
    }
}
