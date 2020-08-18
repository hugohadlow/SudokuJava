package sudoku;

import java.util.concurrent.ThreadLocalRandom;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

public class Sudoku {
    public byte[][] Solve(byte[][] input)
    {
        byte[][] exactCoverProblem = GenerateExactCoverProblem(input);

        ExactCover exactCover = new ExactCover();
        List<HashSet<Integer>> results = exactCover.GetAllSolutions(exactCoverProblem);
        if (results.size() > 1) throw new IllegalArgumentException("More than one solution exists.");
        else return ExactCoverResultToSudoku(results.get(0));
    }

    public byte[][] GenerateRandomCompleteGrid()
    {
        byte[][] input = new byte[9][9];
        for(int i = 0; i < 9; i++) {
            //input[i] = new byte[9];
            for (int j = 0; j < 9; j++) input[i][j]=0;
        }
        byte[][] exactCoverProblem = GenerateExactCoverProblem(input);

        Tuple<byte[][], List<Integer>> randomMatrix = RandomiseMatrix(exactCoverProblem);

        ExactCover exactCover = new ExactCover();
        HashSet<Integer> result = exactCover.GetFirstSolution(randomMatrix.left);
        return ExactCoverResultToSudoku(result, randomMatrix.right);
    }

    public byte[][] GeneratePuzzleWithClues(int n)
    {
        if (n < 17) System.out.println("Not possible!");
        ExactCover exactCover = new ExactCover();
        byte[][] completedSudoku;
        byte[][] cluesRemoved;

        int attempts = 0;

        do
        {
            completedSudoku = GenerateRandomCompleteGrid();
            cluesRemoved = RemoveClues(completedSudoku, 81-n);
            attempts++;
        }
        while (!exactCover.CheckExactlyOneSolution(GenerateExactCoverProblem(cluesRemoved)));

        System.out.println("Attempts: " + attempts);

        Print(completedSudoku);
        System.out.println("#########");
        Print(cluesRemoved);

        return cluesRemoved;
    }

    private byte[][] RemoveClues(byte[][] input, int numberOfCluesToRemove)
    {
        byte[][] output = new byte[9][9];
        List<Tuple<Integer, Integer>> clues = new ArrayList<Tuple<Integer, Integer>>();
        for (int i = 0; i < 9; i++) for (int j = 0; j < 9; j++) clues.add(new Tuple<Integer,Integer>(i, j));
        while(numberOfCluesToRemove > 0)
        {
            int r = ThreadLocalRandom.current().nextInt(0, clues.size());
            clues.remove(r);
            numberOfCluesToRemove--;
        }
        for(Tuple<Integer,Integer> clue : clues)
        {
            output[clue.left][clue.right] = input[clue.left][clue.right];
        }
        return output;
    }

    private byte[][] GenerateExactCoverProblem(byte[][] input) {
        byte[][] exactCoverProblem = new byte[324][729];

        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                for (int z = 0; z < 9; z++)
                {
                    boolean setValue = false;
                    if (input[i][j] == 0) setValue = true;
                        else if (input[i][j] == (z + 1)) setValue = true;

                    if (setValue)
                    {
                        int row = (i * 81) + (j * 9) + z;

                        //Columns
                        //Cell constraints
                        exactCoverProblem[(i * 9) + j][row] = 1;

                        //Row constraints
                        exactCoverProblem[81 + (i * 9) + z][row] = 1;

                        //Column constraints
                        exactCoverProblem[162 + (j * 9) + z][row] = 1;

                        //Box constraints
                        exactCoverProblem[243 + (27 * (i / 3)) + (9 * (j / 3)) + z][row] = 1;

                    }
                }
            }
        }

        return exactCoverProblem;
    }


    private byte[][] ExactCoverResultToSudoku(HashSet<Integer> exactCoverResult){
        return ExactCoverResultToSudoku(exactCoverResult, null);
    }

    private byte[][] ExactCoverResultToSudoku(HashSet<Integer> exactCoverResult,
                                             List<Integer> rowsRandomOrder)
    {
        byte[][] sudoku = new byte[9][9];

        for (int resultRowName : exactCoverResult)
        {
            int rowName;
            if (rowsRandomOrder == null)
                rowName = resultRowName;
            else
                rowName = rowsRandomOrder.get(resultRowName);

            int i = rowName / 81;
            int j = (rowName % 81) / 9;
            byte value = (byte)((rowName % 9) + 1);
            sudoku[i][j] = value;
        }

        return sudoku;
    }

    public Tuple<byte[][], List<Integer>> RandomiseMatrix(byte[][] input)
    {
        int x = input.length;
        int y = input[0].length;
        byte[][] output = new byte[x][y];

        List<Integer> xHeaders = new ArrayList<Integer>();
        List<Integer> yHeaders = new ArrayList<Integer>();
        for(int n = 0; n < x; n++)
        {
            int r = ThreadLocalRandom.current().nextInt(0, n + 1);
            xHeaders.add(r, n);
        }
        for (int n = 0; n < y; n++)
        {
            int r = ThreadLocalRandom.current().nextInt(0, n + 1);
            yHeaders.add(r, n);
        }

        for(int i = 0; i < x; i++)
        {
            for (int j = 0; j < y; j++)
            {
                output[i][j] = input[xHeaders.get(i)][yHeaders.get(j)];
            }
        }

        return new Tuple<>(output, yHeaders);
    }

    private void Print(byte[][] sudoku)
    {
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j < 9; j++)
            {
                System.out.print(sudoku[i][j]);
            }
            System.out.println();
        }
    }
}
