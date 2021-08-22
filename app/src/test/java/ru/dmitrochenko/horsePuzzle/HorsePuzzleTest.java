package ru.dmitrochenko.horsePuzzle;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import ru.dmitrochenko.horsePuzzle.puzzle.HorsePuzzle;

import static org.assertj.core.api.Assertions.assertThat;

class HorsePuzzleTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 3, 5, 20, 21, 22, 25, 35, 41, 42, 43, 50, 63})
    void writeReadPath(int size) {
        HorsePuzzle hp = new HorsePuzzle(4, 4, 0);
        long[] path = new long[1];

        int[] in = new Random().ints(size, 0, 8).toArray();
        for (int move : in) {
            path = hp.writeMove((byte) move, path);
        }

        int pathSize;
        if (in.length <= 21) {
            pathSize = 1;
        } else if (in.length <= 42) {
            pathSize = 2;
        } else {
            pathSize = 3;
        }

        assertThat(path).hasSize(pathSize);
        assertThat(hp.getMoveCount()).isEqualTo(in.length);
        byte[] out = hp.readPath(path);

        for (int i = 0; i < in.length; i++) {
            assertThat(out[i]).isEqualTo((byte) in[i]);
        }
    }

    @Test
    void convertToBoardTest() {
        HorsePuzzle hp = new HorsePuzzle(4, 5, 0);
        long[] path = new long[1];

        final int[][] moveOffset = new int[][]
                {{+1, +2}, {-1, +2}, {-2, +1}, {-2, -1}, {-1, -2}, {+1, -2}, {+2, -1}, {+2, +1}};

        long[] board;

        path = hp.writeMove((byte) 0, path);
        board = hp.convertToBoard(path);
        hp.drawInConsole(board[0], (byte) board[1]);

        path = hp.writeMove((byte) 5, path);
        board = hp.convertToBoard(path);
        hp.drawInConsole(board[0], (byte) board[1]);

        path = hp.writeMove((byte) 0, path);
        board = hp.convertToBoard(path);
        hp.drawInConsole(board[0], (byte) board[1]);

        path = hp.writeMove((byte) 2, path);
        board = hp.convertToBoard(path);
        hp.drawInConsole(board[0], (byte) board[1]);

        path = hp.writeMove((byte) 5, path);
        board = hp.convertToBoard(path);
        hp.drawInConsole(board[0], (byte) board[1]);

        path = hp.writeMove((byte) 0, path);
        board = hp.convertToBoard(path);
        hp.drawInConsole(board[0], (byte) board[1]);
    }

    @Test
    void getAvailableMovesTest() {
        HorsePuzzle hp = new HorsePuzzle(4, 5, 0);
        long[] path = new long[1];

        final int[][] moveOffset = new int[][]
                {{+1, +2}, {-1, +2}, {-2, +1}, {-2, -1}, {-1, -2}, {+1, -2}, {+2, -1}, {+2, +1}};

        long[] board;
        byte[] avail;
        board = hp.convertToBoard(path);
        hp.drawInConsole(board[0], (byte) board[1]);
        avail = hp.getAvailableMoves(board[0], (byte) board[1]);
        System.out.println(Arrays.toString(avail));

        path = hp.writeMove((byte) 0, path);
        hp.moveCount++;
        board = hp.convertToBoard(path);
        hp.drawInConsole(board[0], (byte) board[1]);
        avail = hp.getAvailableMoves(board[0], (byte) board[1]);
        System.out.println(Arrays.toString(avail));

        path = hp.writeMove((byte) 5, path);
        hp.moveCount++;
        board = hp.convertToBoard(path);
        hp.drawInConsole(board[0], (byte) board[1]);
        avail = hp.getAvailableMoves(board[0], (byte) board[1]);
        System.out.println(Arrays.toString(avail));

        path = hp.writeMove((byte) 0, path);
        hp.moveCount++;
        board = hp.convertToBoard(path);
        hp.drawInConsole(board[0], (byte) board[1]);
        avail = hp.getAvailableMoves(board[0], (byte) board[1]);
        System.out.println(Arrays.toString(avail));

        path = hp.writeMove((byte) 2, path);
        hp.moveCount++;
        board = hp.convertToBoard(path);
        hp.drawInConsole(board[0], (byte) board[1]);
        avail = hp.getAvailableMoves(board[0], (byte) board[1]);
        System.out.println(Arrays.toString(avail));

        path = hp.writeMove((byte) 5, path);
        hp.moveCount++;
        board = hp.convertToBoard(path);
        hp.drawInConsole(board[0], (byte) board[1]);
        avail = hp.getAvailableMoves(board[0], (byte) board[1]);
        System.out.println(Arrays.toString(avail));

        path = hp.writeMove((byte) 0, path);
        hp.moveCount++;
        board = hp.convertToBoard(path);
        hp.drawInConsole(board[0], (byte) board[1]);
        avail = hp.getAvailableMoves(board[0], (byte) board[1]);
        System.out.println(Arrays.toString(avail));
    }


    @RepeatedTest(1)

        // 4/5 - 9 5/5 - 33 5/6 - 400 |||| 5/5 - 30 5/6 - 350 ||||| 4/5 - 6 5/5 - 25 5/6 - 305 ||||| 4/5 - 5 5/5 - 22.5 5/6 - 330 |||||| 5/5 - 19 5/6 - 300 ||||
        // 5/5 - 12 5/6 - 155 6/6
        //@Test
    void calculate() {
        int rows = 5;
        int cols = 5;
        for (int i = 0; i <rows*cols - 1 ; i++) {
            System.out.println(i);
            HorsePuzzle hp = new HorsePuzzle(rows, cols, i);
            hp.calculate();
        }

    }

    @RepeatedTest(1)
        //6/6 - 40s (10000) 6/6 - 33 (1000(100)) 6/6 - 28 (1000) 6/6 - 12s (1000) 6/7 (1000000) -23 min
    void calculateByParts() {
        HorsePuzzle hp = new HorsePuzzle(7, 7, 0);
        hp.calculateByParts();
    }

    // 4/5 - 2.5 // 5/5 - 14 5/6 -

    @Test
    void initMoveMove() {
        HorsePuzzle hp = new HorsePuzzle(4, 5, 0);
    }


    @Test
    void getAvailFromMap() {
        HorsePuzzle hp = new HorsePuzzle(4, 5, 0);
        long[] path = new long[1];

        final int[][] moveOffset = new int[][]
                {{+1, +2}, {-1, +2}, {-2, +1}, {-2, -1}, {-1, -2}, {+1, -2}, {+2, -1}, {+2, +1}};

        long[] board;
        byte[] avail;
        board = hp.convertToBoard(path);
        hp.drawInConsole(board[0], (byte) board[1]);
        avail = hp.getAvailableMoveFromMap(board[0], (byte) board[1]);
        System.out.println(Arrays.toString(avail));

        path = hp.writeMove((byte) 0, path);
        hp.moveCount++;
        board = hp.convertToBoard(path);
        hp.drawInConsole(board[0], (byte) board[1]);
        avail = hp.getAvailableMoveFromMap(board[0], (byte) board[1]);
        System.out.println(Arrays.toString(avail));

        path = hp.writeMove((byte) 5, path);
        hp.moveCount++;
        board = hp.convertToBoard(path);
        hp.drawInConsole(board[0], (byte) board[1]);
        avail = hp.getAvailableMoveFromMap(board[0], (byte) board[1]);
        System.out.println(Arrays.toString(avail));

        path = hp.writeMove((byte) 0, path);
        hp.moveCount++;
        board = hp.convertToBoard(path);
        hp.drawInConsole(board[0], (byte) board[1]);
        avail = hp.getAvailableMoveFromMap(board[0], (byte) board[1]);
        System.out.println(Arrays.toString(avail));

        path = hp.writeMove((byte) 2, path);
        hp.moveCount++;
        board = hp.convertToBoard(path);
        hp.drawInConsole(board[0], (byte) board[1]);
        avail = hp.getAvailableMoveFromMap(board[0], (byte) board[1]);
        System.out.println(Arrays.toString(avail));

        path = hp.writeMove((byte) 5, path);
        hp.moveCount++;
        board = hp.convertToBoard(path);
        hp.drawInConsole(board[0], (byte) board[1]);
        avail = hp.getAvailableMoveFromMap(board[0], (byte) board[1]);
        System.out.println(Arrays.toString(avail));

        path = hp.writeMove((byte) 0, path);
        hp.moveCount++;
        board = hp.convertToBoard(path);
        hp.drawInConsole(board[0], (byte) board[1]);
        avail = hp.getAvailableMoveFromMap(board[0], (byte) board[1]);
        System.out.println(Arrays.toString(avail));
    }

    @RepeatedTest(20)
    void parallel() {
        List<Integer> ints = new ArrayList<>();
        for (int i = 0; i < 100_000_0; i++) {
            ints.add(i);
        }

        long start = System.currentTimeMillis();
        List<Integer> result = ints.stream().filter(v -> v % 2 == 0).collect(Collectors.toList());

        System.out.println(System.currentTimeMillis() - start);
        System.out.println(result.size());
    }

    @RepeatedTest(10)
    void limit() {
        HorsePuzzle hp = new HorsePuzzle(8, 8, 0);
        hp.calculateLimitPosition(0,0,0);
    }

}
