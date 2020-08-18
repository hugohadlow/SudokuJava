package sudoku;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExactCoverTests {

    @BeforeEach
    void setUp() {}

    @AfterEach
    void tearDown() {}

    @Test
    public void Solve()
    {
        ExactCover exactCover = new ExactCover();
        byte[][] input = {
            { 0, 1, 0, 1, 0, 0 },
            { 0, 0, 1, 0, 1, 0 },
            { 1, 0, 1, 0, 0, 0 },
            { 0, 1, 0, 1, 0, 1 },
            { 1, 0, 0, 0, 0, 1 },
            { 1, 0, 1, 0, 0, 0 },
            { 0, 1, 0, 0, 1, 1 },
        };

        List<HashSet<Integer>> results = exactCover.GetAllSolutions(input);
        assertEquals(1, results.size());
        HashSet<Integer> result = results.get(0);
        assertEquals(3, result.size());
        assertTrue(result.contains(0));
        assertTrue(result.contains(3));
        assertTrue(result.contains(4));
    }

    @Test
    public void Solve_2()
    {
        ExactCover exactCover = new ExactCover();
        byte[][] input = {
            { 1, 1, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 1, 1 },
            { 0, 0, 0, 1, 1, 0 },
            { 1, 1, 1, 0, 0, 0 },
            { 0, 0, 1, 1, 0, 0 },
            { 0, 0, 0, 1, 1, 0 },
            { 1, 0, 1, 0, 1, 1 },
        };

        List<HashSet<Integer>>  results = exactCover.GetAllSolutions(input);
        assertEquals(1, results.size());
        HashSet<Integer> result = results.get(0);
        assertEquals(3, result.size());
        assertTrue(result.contains(1));
        assertTrue(result.contains(3));
        assertTrue(result.contains(5));
    }

    @Test
    public void Solve_RowAllZero()
    {
        ExactCover exactCover = new ExactCover();
        byte[][] input = {
            { 0, 1, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 1, 1 },
            { 0, 0, 0, 1, 1, 0 },
            { 0, 1, 1, 0, 0, 0 },
            { 0, 0, 1, 1, 0, 0 },
            { 0, 0, 0, 1, 1, 0 },
            { 0, 0, 1, 0, 1, 1 },
        };

        List<HashSet<Integer>>  results = exactCover.GetAllSolutions(input);
        assertEquals(1, results.size());
        HashSet<Integer> result = results.get(0);
        assertEquals(3, result.size());
        assertTrue(result.contains(1));
        assertTrue(result.contains(3));
        assertTrue(result.contains(5));
    }

    @Test
    public void Solve_NoResults()//Because empty column cannot be satisfied
    {
        ExactCover exactCover = new ExactCover();
        byte[][] input =  {
            { 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 1, 1 },
            { 0, 0, 0, 1, 1, 0 },
            { 1, 1, 1, 0, 0, 0 },
            { 0, 0, 1, 1, 0, 0 },
            { 0, 0, 0, 1, 1, 0 },
            { 1, 0, 1, 0, 1, 1 },
        };

        List<HashSet<Integer>> results = exactCover.GetAllSolutions(input);
        assertEquals(0, results.size());
    }

    @Test
    public void Solve_MultipleResults()
    {
        ExactCover exactCover = new ExactCover();
        byte[][] input = {
            { 1, 0, 1, 0 },
            { 1, 0, 1, 0 },
            { 1, 0, 0, 1 },
            { 1, 0, 0, 1 },
            { 0, 1, 1, 0 },
            { 0, 1, 1, 0 },
            { 0, 1, 0, 1 },
        };

        List<HashSet<Integer>> results = exactCover.GetAllSolutions(input);
        assertEquals(2, results.size());
        HashSet<Integer> result1 = results.get(0);
        assertEquals(2, result1.size());
        assertTrue(result1.contains(0));
        assertTrue(result1.contains(1));
        HashSet<Integer> result2 = results.get(1);
        assertEquals(2, result2.size());
        assertTrue(result2.contains(2));
        assertTrue(result2.contains(3));
    }


    @Test
    public void GetFirstSolution()
    {
        ExactCover exactCover = new ExactCover();
        byte[][] input =  {
            { 1, 0, 1, 0 },
            { 1, 0, 1, 0 },
            { 1, 0, 0, 1 },
            { 1, 0, 0, 1 },
            { 0, 1, 1, 0 },
            { 0, 1, 1, 0 },
            { 0, 1, 0, 1 },
        };

        HashSet<Integer> result = exactCover.GetFirstSolution(input);
        assertTrue(result.contains(0));
        assertTrue(result.contains(1));
    }

    @Test
    public void CheckMultipleSolutions_True()
    {
        ExactCover exactCover = new ExactCover();
        byte[][] input = {
            { 1, 0, 1, 0 },
            { 1, 0, 1, 0 },
            { 1, 0, 0, 1 },
            { 1, 0, 0, 1 },
            { 0, 1, 1, 0 },
            { 0, 1, 1, 0 },
            { 0, 1, 0, 1 },
        };

        boolean result = exactCover.MoreThanOneSolution(input);
        assertTrue(result);
    }

    @Test
    public void CheckMultipleSolutions_False()
    {
        ExactCover exactCover = new ExactCover();
        byte[][] input = {
            { 1, 0, 1, 0 },
            { 1, 0, 1, 0 },
            { 1, 0, 0, 1 },
            { 1, 0, 0, 1 },
            { 0, 1, 0, 0 },
            { 0, 1, 1, 0 },
            { 0, 1, 0, 1 },
        };

        boolean result = exactCover.MoreThanOneSolution(input);
        assertFalse(result);
    }
}