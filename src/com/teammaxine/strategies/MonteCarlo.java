package com.teammaxine.strategies;

import aiproj.slider.Move;
import com.teammaxine.board.actions.AgentAction;
import com.teammaxine.board.elements.Board;
import com.teammaxine.board.helpers.Scorer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by noxm on 10/05/17.
 */
public class MonteCarlo implements Strategy {
    private char player;
    private Random random;
    private static final int TRIES = 5000;
    private static final int MAX_DEPTH = 150;

    private static Move prevMove;

    public MonteCarlo(char player) {
        prevMove = null;
        this.player = player;
        this.random = new Random(System.currentTimeMillis());
    }

    private Move oppositeMove(Move m) {
        Move.Direction opDir;
        int i = m.i;
        int j = m.j;
        if(m.d == Move.Direction.UP) {
            opDir = Move.Direction.DOWN;
            j++;
        } else if(m.d == Move.Direction.DOWN) {
            opDir = Move.Direction.UP;
            j--;
        } else if(m.d == Move.Direction.LEFT) {
            opDir = Move.Direction.LEFT;
            i--;
        } else {
            opDir = Move.Direction.RIGHT;
            i++;
        }
        return new Move(i, j, opDir);
    }

    @Override
    public Move findMove(Board currentBoard, int depth) {
        ArrayList<AgentAction> moves;

        if(player == 'H') {
            moves = currentBoard.getHorizontal().getOptimisticMoves();
        } else {
            moves = currentBoard.getVertical().getOptimisticMoves();
        }

        double maxScore = Double.NEGATIVE_INFINITY;
        Move toMake = null;

        for(Move m : moves) {
            double sum = 0;
            double localMax = Double.NEGATIVE_INFINITY;
            double localMin = Double.POSITIVE_INFINITY;

            Board copyBoard = new Board(currentBoard);
            for(int i = 0; i < TRIES; i++) {
                double score = randomTraverse(copyBoard, m, 0, player, player);
                sum += score;

                if(score >= localMax)
                    localMax = score;
                if(score <= localMin)
                    localMin = score;
            }
            double avg = sum / TRIES;

            if((avg >= maxScore) && (prevMove == null || oppositeMove(prevMove).toString().equals(m.toString()))) {
                maxScore = avg;
                toMake = m;
            }
        }

        prevMove = toMake;

        return toMake;
    }

    private double randomTraverse(Board board, Move m, int depth, char turn, char player) {
        if(m != null)
            board.makeMove(m, turn);

        if((board.getHorizontal().getMyCells().size() == 0 || board.getVertical().getMyCells().size() == 0) ||
                depth >= MAX_DEPTH ||
                (board.getHorizontal().getLegalMoves().size() == 0 && board.getVertical().getLegalMoves().size() == 0)) {
            char winner = board.getWinner();
            if(winner == '-') {
                // no winner
                return 0;
                //return Math.tanh(Scorer.scoreBoard(board, player));
            } else if(winner == player) {
                return 1;
            }
            return -1;
        }

        char nextTurn = (turn == 'H')? 'V' : 'H';

        ArrayList<AgentAction> moves;

        if(nextTurn == 'H')
            moves = board.getHorizontal().getOptimisticMoves();
        else
            moves = board.getVertical().getOptimisticMoves();

        if(moves.size() > 0) {
            Move randomMove = moves.get(random.nextInt(moves.size()));
            return randomTraverse(board, randomMove, depth + 1, nextTurn, player);
        } else {
            return randomTraverse(board, null, depth + 1, nextTurn, player);
        }
    }
}
