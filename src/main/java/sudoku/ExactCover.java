package sudoku;

import java.util.*;

public class ExactCover
{
    private class Cell {
        public Cell Right;
        public Cell Left;
        public Cell Up;
        public Cell Down;

        public int ColumnName; //For debugging
        public int RowName;
    }

    private class ColumnHeader extends Cell
    {
        public int Size;
    }

    private ColumnHeader Root;
    private Stack<Integer> Stack;
    private List<HashSet<Integer>> Solutions;

    //Constraints go in rows, not columns when formatting for input!
    //But in the code, constraints are columns. First dimension.
    //Constraint columns cannot be all zero otherwise that constraint cannot be satisfied by any row.
    //But rows can be all zero, they will be ignored.
    private void Initialise(byte[][] input)
    {
        Root = new ColumnHeader();

        int columns = input.length;
        int rows = input[0].length;
        ColumnHeader[] columnHeaders = new ColumnHeader[columns];
        for (int i = 0; i < columns; i++)
        {
            ColumnHeader columnHeader = new ColumnHeader();
            columnHeader.ColumnName = i;
            columnHeaders[i] = columnHeader;
        }

        Cell[][] cells = new Cell[columns][rows];
        for (int i = 0; i < columns; i++)
        {
            ColumnHeader columnHeader = columnHeaders[i];
            if (i == 0) columnHeader.Left = Root;
            else columnHeader.Left = columnHeaders[i - 1];
            if (i == (columns - 1)) columnHeader.Right = Root;
            else columnHeader.Right = columnHeaders[i + 1];

            //Establish columns
            Cell previousCell = columnHeader;
            for (int j = 0; j < rows; j++)
            {
                if (input[i][j] != 0)
                {
                    columnHeader.Size++;
                    Cell cell = new Cell();
                    cell.ColumnName = i;
                    cell.RowName = j;
                    cells[i][j] = cell;
                    cell.Up = previousCell;
                    previousCell = cell;
                }
            }
            columnHeader.Up = previousCell;

            //Add down links.
            columnHeader.Up.Down = columnHeader;
            Cell current = columnHeader.Up;
            while (current != columnHeader)
            {
                current.Up.Down = current;
                current = current.Up;
            }
        }

        Root.Right = columnHeaders[0];
        Root.Left = columnHeaders[columns - 1];

        for (int j = 0; j < rows; j++)
        {
            Cell firstCellInRow = null;
            Cell previousCell = null;
            for (int i = 0; i < columns; i++)
            {
                if (input[i][j] != 0)
                {
                    if (firstCellInRow == null)
                    {
                        firstCellInRow = cells[i][j];
                    }
                    else
                    {
                        cells[i][j].Left = previousCell;
                    }
                    previousCell = cells[i][j];
                }
            }
            if (firstCellInRow != null) //Row could be empty
            {
                firstCellInRow.Left = previousCell;

                //Add right links.
                firstCellInRow.Left.Right = firstCellInRow;
                Cell current = firstCellInRow.Left;
                while (current != firstCellInRow)
                {
                    current.Left.Right = current;
                    current = current.Left;
                }
            }
        }
    }

    public boolean MoreThanOneSolution(byte[][] input)
    {
        Initialise(input);
        SearchForMultipleSolutions(0);
        return Solutions.size() > 1;
    }

    public HashSet<Integer> GetFirstSolution(byte[][] input)
    {
        Initialise(input);
        SearchForFirstSolution(0);
        return Solutions.get(0);
    }

    public boolean CheckExactlyOneSolution(byte[][] input)
    {
        if (MoreThanOneSolution(input)) return false;

        Initialise(input);
        Search(0);
        return Solutions.size() == 1;
    }

    public List<HashSet<Integer>> GetAllSolutions(byte[][] input)
    {
        Initialise(input);
        Search(0);
        return Solutions;
    }

    private void Search(int n)
    {
        Search(n, false, false);
    }

    private void SearchForFirstSolution(int n)
    {
        Search(n, true, false);
    }

    private void SearchForMultipleSolutions(int n)
    {
        Search(n, false, true);
    }

    private void Search(int n, boolean getFirstSolution, boolean checkForMultipleSolutions)
    {
        if (n == 0)
        {
            Solutions = new ArrayList<HashSet<Integer>>();
            Stack = new Stack<Integer>();
        }

        if (getFirstSolution && Solutions.size() > 0) return;
        if (checkForMultipleSolutions && Solutions.size() > 1) return;

        //PrintState();
        if (Root.Right == Root) //No columns remain to be covered
        {
            Solutions.add(new HashSet<>(Stack));
            return;
        }

        ColumnHeader column = SmallestColumn(); //This biases the ordering of solutions but is essential for speed.
        CoverColumn(column);
        for(Cell cell : GetCellsInColumn(column))
        {
            Stack.push(cell.RowName);

            List<Cell> otherCellsInRow = GetOtherCellsInRow(cell);
            for (Cell cellInRow : otherCellsInRow)
            {
                CoverColumn(cellInRow);
            }
            Search(n + 1, getFirstSolution, checkForMultipleSolutions);

            Collections.reverse(otherCellsInRow);
            for (Cell cellInRow : otherCellsInRow)
            {
                UncoverColumn(cellInRow);
            }

            Stack.pop();
        }
        UncoverColumn(column);
    }

    private Iterable<Cell> GetCellsInColumn(ColumnHeader columnHeader)
    {
        return GetCellsInColumnDownwards(columnHeader);
    }

    private List<Cell> GetCellsInColumnDownwards(ColumnHeader columnHeader)
    {
        Cell cell = columnHeader.Down;
        List<Cell> result = new ArrayList<Cell>();
        while (cell != columnHeader)
        {
            result.add(cell);
            cell = cell.Down;
        }
        return result;
    }

    private List<Cell> GetCellsInColumnUpwards(ColumnHeader columnHeader)
    {
        Cell cell = columnHeader.Up;
        List<Cell> result = new ArrayList<Cell>();
        while (cell != columnHeader)
        {
            result.add(cell);
            cell = cell.Up;
        }
        return result;
    }

    private ColumnHeader SmallestColumn()
    {
        ColumnHeader smallestColumn = (ColumnHeader)Root.Right;
        ColumnHeader column = (ColumnHeader)Root.Right;
        while (column != Root)
        {
            if (column.Size < smallestColumn.Size) smallestColumn = column;
            column = (ColumnHeader)column.Right;
        }
        return smallestColumn;
    }

    private ColumnHeader GetColumnHeader(Cell cell)
    {
        while (!(cell instanceof ColumnHeader))
        {
            cell = cell.Up;
        }
        return (ColumnHeader)cell;
    }

    private void CoverColumn(Cell cell)
    {
        CoverColumn(GetColumnHeader(cell));
    }

    private void UncoverColumn(Cell cell)
    {
        UncoverColumn(GetColumnHeader(cell));
    }

    private void CoverColumn(ColumnHeader columnHeader)
    {
        columnHeader.Right.Left = columnHeader.Left;
        columnHeader.Left.Right = columnHeader.Right;
        for (Cell cell : GetCellsInColumnDownwards(columnHeader))
        {
            for (Cell rowCell : GetOtherCellsInRow(cell))
            {
                rowCell.Down.Up = rowCell.Up;
                rowCell.Up.Down = rowCell.Down;
                GetColumnHeader(rowCell).Size--;
            }
        }
    }

    private void UncoverColumn(ColumnHeader columnHeader)
    {
        for (Cell cell : GetCellsInColumnUpwards(columnHeader))
        {
            for (Cell rowCell : GetOtherCellsInRow(cell))
            {
                GetColumnHeader(rowCell).Size++;
                rowCell.Down.Up = rowCell;
                rowCell.Up.Down = rowCell;
            }
        }
        columnHeader.Right.Left = columnHeader;
        columnHeader.Left.Right = columnHeader;
    }

    private List<Cell> GetOtherCellsInRow(Cell cell)
    {
        //Do not return the cell itself.
        Cell next = cell.Right;

        List<Cell> result = new ArrayList<Cell>();
        while(next != cell)
        {
            result.add(next);
            next = next.Right;
        }
        return result;
    }

    private void PrintState()
    {
        System.out.println("Current state:");

        for(int j=-1; j<6; j++)
        {
            ColumnHeader column = (ColumnHeader)Root.Right;
            while (column != Root)
            {
                if (j==-1) System.out.print(column.ColumnName);
                else
                {
                    boolean present = false;
                    for (Cell cell : GetCellsInColumnDownwards(column))
                    {
                        if (cell.RowName == j) present = true;
                    }
                    if (present) System.out.print("1"); else System.out.print("0");
                }

                column = (ColumnHeader)column.Right;
            }
            System.out.println();
        }

        System.out.println("#######");
    }
}
