package ru.dmitrochenko.horsePuzzle.puzzle;


import java.util.*;

import static java.util.stream.Collectors.*;

public class HorsePuzzle {
    final int cols;
    final int rows;
    final int start;
    final int shift;

    long[] maskMap;
    byte[][][] moveMap;
    byte[] moveByDiff;

    private int moveCount;
    private boolean isCanceled;

    private List<byte[]> hintsPaths = new ArrayList<>();
    private List<Byte> hintsMoves = new ArrayList<>();
    private List<long[]> startList = new ArrayList<>();

    static final int[][] moveOffset = new int[][]
            {{+1, +2}, {-1, +2}, {-2, +1}, {-2, -1}, {-1, -2}, {+1, -2}, {+2, -1}, {+2, +1}};
    //    0          1         2        3         4          5          6        7

    public HorsePuzzle(int rows, int cols, int start) {
        this.cols = cols;
        this.rows = rows;
        this.start = start;
        this.shift = 2 * cols + 1;
        initMoveAndMaskMap();
        initMoveByDiffMap();
    }

    private void initMoveByDiffMap() {
        moveByDiff = new byte[2 * shift + 1];

        moveByDiff[-2 * cols - 1 + shift] = 4;
        moveByDiff[-2 * cols + 1 + shift] = 5;
        moveByDiff[-cols + 2 + shift] = 6;
        moveByDiff[-cols - 2 + shift] = 3;
        moveByDiff[2 * cols + 1 + shift] = 0;
        moveByDiff[2 * cols - 1 + shift] = 1;
        moveByDiff[cols - 2 + shift] = 2;
        moveByDiff[cols + 2 + shift] = 7;
    }

    private void initMoveAndMaskMap() {
        moveMap = new byte[rows * cols][3][];
        maskMap = new long[rows * cols];
        long board = 0;
        board = setBit(board, (byte) start);

        for (int position = 0; position < moveMap.length; position++) {
            byte[] availableMoves = getAvailableMoves(board, position);
            byte[] moveNumberArr = new byte[availableMoves.length];
            byte[] movePositionArr = new byte[availableMoves.length];

            for (int moveNumber = 0; moveNumber < availableMoves.length; moveNumber++) {
                byte movePosition = getMovePosition(availableMoves[moveNumber], position);
                moveNumberArr[moveNumber] = availableMoves[moveNumber];
                movePositionArr[moveNumber] = movePosition;
            }
            long mask = 0;
            for (byte movePosition : movePositionArr) {
                mask = setBit(mask, movePosition);
            }

            byte[] allPosition = new byte[8];
            for (byte i = 0; i <= 7; i++) {
                allPosition[i] = getMovePosition(i, position);
            }

            moveMap[position][0] = moveNumberArr;
            moveMap[position][1] = movePositionArr;
            moveMap[position][2] = allPosition;
            maskMap[position] = mask;
        }
    }

    public void cancelCalculate() {
        isCanceled = true;
    }

    private int initCalculate(int limit) {
        isCanceled = false;
        hintsMoves.clear();
        hintsPaths.clear();
        startList.clear();
        return (limit == Integer.MAX_VALUE) ? 2 : 0;
    }

    private void initPath(long board, long start) {
        long[] p = new long[5];
        p[3] = board;
        p[4] = start;
        startList.add(p);
        moveCount = 0;
    }

    public long calculateLimitPosition(long board, int from, int madeMoves, int limit, boolean finisOnStart) {
        int iteration = initCalculate(limit);
        while (startList.isEmpty() && iteration != 3) {
            initPath(board, from);
            for (int i = 0; i < rows * cols - 1 - madeMoves; i++) {
                if (isCanceled) return -1;
                Collections.shuffle(startList);
                startList = startList.stream().unordered().limit(limit).parallel().map(this::proceedBoard)
                        .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
                if (startList.isEmpty()) break;
                moveCount++;
            }
            iteration++;
        }
        if (finisOnStart) {
            startList = startList.stream().filter(this::canReachStart).collect(toList());
        }
        initHints(from, madeMoves, startList);
        return startList.size();
    }

    private boolean canReachStart(long[] p) {
        long last = p[4];
        byte[] availFromStart = getAvailablePosition(0L, start);
        for (byte b : availFromStart) {
            if (b == (byte) last) {
                return true;
            }
        }
        return false;
    }


    private void initHints(int from, int movesCount, List<long[]> startList) {
        if (!startList.isEmpty()) {
            int moves = rows * cols - 1 - movesCount;
            hintsPaths = startList.stream().map(p -> readPath(p, moves)).collect(toList());
            hintsMoves = hintsPaths.stream().map(p -> p[0]).distinct().map(m -> getMovePosition(m, from)).collect(toList());
        }
    }

    public List<byte[]> getHintsPaths() {
        return hintsPaths;
    }

    public List<Byte> getHintsMoves() {
        return hintsMoves;
    }

    private List<long[]> proceedBoard(long[] path) {
        int last = (int) path[4];
        long board = path[3];
        long mask = maskMap[last];

        if (Long.bitCount(board & mask) == Long.bitCount(mask)) {
            return Collections.emptyList();
        }

        List<long[]> boards = null;
        byte movePosition = 0;
        long avail = ~board & mask;

        while (avail != 0 && movePosition != 64) {
            int countOfZeros = Long.numberOfTrailingZeros(avail);
            movePosition += countOfZeros;
            avail = avail >> (countOfZeros + 1);
            long b = setBit(board, movePosition);
            if ((Long.bitCount(board) > rows * cols - 3) || checkBoard(b)) {
                if (boards == null) {
                    boards = new ArrayList<>();
                }
                long[] copy = new long[5];
                copy[0] = path[0];
                copy[1] = path[1];
                copy[2] = path[2];
                copy[3] = b;
                copy[4] = movePosition;
                writeMove(moveByDiff[movePosition - last + shift], copy);
                boards.add(copy);
            }
            movePosition++;
        }
        return boards == null ? Collections.emptyList() : boards;
    }

    public boolean checkBoard(long board) {
        long empty = ~board;
        int position = Long.numberOfTrailingZeros(empty);
        empty = empty >> (position + 1);

        while (position < rows * cols) {
            long mask = maskMap[position];
            if (Long.bitCount(board & mask) == Long.bitCount(mask)) {
                return false;
            }

            int checkPosition = position;
            long checkBoard = board;

            for (int i = 0; i < 6; i++) {
                long avail = ~checkBoard & mask;
                if (Long.bitCount(avail) == 1 && Long.bitCount(checkBoard) < rows * cols - 2) {
                    checkBoard = setBit(checkBoard, (byte) checkPosition);
                    checkPosition = Long.numberOfTrailingZeros(avail);
                    mask = maskMap[checkPosition];
                    if (Long.bitCount(checkBoard & mask) == Long.bitCount(mask)) {
                        return false;
                    }
                } else {
                    break;
                }
            }
            position += Long.numberOfTrailingZeros(empty) + 1;
            empty = empty >> (Long.numberOfTrailingZeros(empty) + 1);
        }
        return true;
    }

    public void writeMove(byte move, long[] path) {
        int partNumber = moveCount / 21;
        path[partNumber] = writeMove(move, path[partNumber]);
    }

    public long writeMove(byte move, long path) {
        return path == 0 ? move : (path << 3) | move & 0xff;
    }

    public byte[] readLong(long part) {
        return readLong(part, 21);
    }

    public byte[] readLong(long part, int moves) {
        byte[] result = new byte[moves];
        for (int i = 0; i < moves; i++) {
            long rightShifted = part >>> (3 * i);
            long mask = (1L << 3) - 1L;
            result[moves - i - 1] = (byte) (rightShifted & mask);
        }
        return result;
    }

    public byte[] readPath(long[] path, int readMoveCount) {
        byte[] result = new byte[readMoveCount];
        if (readMoveCount <= 21) {
            return readLong(path[0], readMoveCount);
        } else if (readMoveCount <= 42) {
            System.arraycopy(readLong(path[0]), 0, result, 0, 21);
            System.arraycopy(readLong(path[1], readMoveCount - 21), 0, result, 21, readMoveCount - 21);
        } else {
            System.arraycopy(readLong(path[0]), 0, result, 0, 21);
            System.arraycopy(readLong(path[1]), 0, result, 21, 21);
            System.arraycopy(readLong(path[2], readMoveCount - 42), 0, result, 42, readMoveCount - 42);
        }
        return result;
    }

    public byte[] getAvailableMoveFromMap(long board, int fromPosition) {
        long mask = maskMap[fromPosition];
        long compare = board & mask;

        if (compare == 0) {
            return moveMap[fromPosition][0];
        }
        if (Long.bitCount(compare) == Long.bitCount(mask)) {
            return new byte[0];
        }

        byte[] avail = new byte[Long.bitCount(mask) - Long.bitCount(compare)];
        if (avail.length == 1) {
            long positionBit = Long.numberOfTrailingZeros(mask & ~board);
            long dif = positionBit - fromPosition + shift;
            avail[0] = moveByDiff[(int) dif];
            return avail;
        }

        byte count = 0;
        for (byte i = 0; i < moveMap[fromPosition][0].length; i++) {
            if (!isFill(board, moveMap[fromPosition][1][i])) {
                avail[count++] = moveMap[fromPosition][0][i];
            }
            if (count == avail.length) {
                return avail;
            }
        }
        return avail;
    }

    public byte[] getAvailableMoves(long board, int fromPosition) {
        byte[] availPosition = new byte[8];
        int count = 0;
        int posRow = (fromPosition / cols);
        int posCol = fromPosition - posRow * cols;

        for (byte i = 0; i < moveOffset.length; i++) {
            int[] move = moveOffset[i];
            int moveRow = posRow + move[1];
            int moveCol = posCol + move[0];
            if (moveRow < 0 || moveCol < 0 || moveRow > rows - 1 || moveCol > cols - 1) continue;
            int movePosition = moveRow * cols + moveCol;
            if (!(isFill(board, movePosition))) {
                availPosition[count] = i;
                count++;
            }
        }

        byte[] result = new byte[count];
        System.arraycopy(availPosition, 0, result, 0, count);
        return result;
    }

    public byte[] getAvailablePosition(long board, int fromPosition) {
        byte[] moves = getAvailableMoveFromMap(board, fromPosition);
        byte[] positions = new byte[moves.length];
        for (int i = 0; i < moves.length; i++) {
            positions[i] = getMovePosition(moves[i], fromPosition);
        }
        return positions;
    }

    public long setBit(final long board, final byte position) {
        return board | (1L << position);
    }

    public long unSetBit(long board, int position) {
        board &= ~(1L << position);
        return board;
    }

    public byte getMovePosition(byte move, int fromPosition) {
        int[] m = moveOffset[move];
        int posRow = (fromPosition / cols);
        int posCol = fromPosition - posRow * cols;
        int moveRow = posRow + m[1];
        int moveCol = posCol + m[0];
        return (byte) (moveRow * cols + moveCol);
    }

    public boolean isFill(long board, int position) {
        return board << ~position < 0;
    }
}