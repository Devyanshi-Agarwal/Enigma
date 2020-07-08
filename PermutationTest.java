package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Devyanshi Agarwal
 */
public class PermutationTest {


    /**
     * Testing time limit.
     */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Alphabet alphabet1 = new Alphabet("DEVYANSHI");
    private Alphabet alphabet2 = new Alphabet("ABC123DEF456GH!@78J0");
    private Alphabet alphabet3 = new Alphabet("ABCD");
    private Permutation perm;
    private String alpha = UPPER_STRING;

    /**
     * Check that perm has an alphabet whose size is that of
     * FROMALPHA and TOALPHA and that maps each character of
     * FROMALPHA to the corresponding character of FROMALPHA, and
     * vice-versa. TESTID is used in error messages.
     */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                    e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                    c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                    ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                    ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);

    }

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet() {
        Permutation p = new Permutation("(DEVY) (ANS) (H)", alphabet1);
        p.invert('F');
        Permutation q = new Permutation("(@123ACB)  (7546) (D!0)", alphabet2);
        q.invert('L');
        q.invert('9');
    }
    @Test
    public void testInvert() {
        Permutation p = new Permutation("(DEVY) (ANS) (H)", alphabet1);
        assertEquals('D', p.invert('E'));
        assertEquals('Y', p.invert('D'));
        assertEquals('V', p.invert('Y'));
        assertEquals('A', p.invert('N'));
        assertEquals('S', p.invert('A'));
        assertEquals('H', p.invert('H'));
        Permutation q = new Permutation("(@123ACB)  (7546) (D!0)", alphabet2);
        assertEquals('C', q.invert('B'));
        assertEquals('@', q.invert('1'));
        assertEquals('B', q.invert('@'));
        assertEquals('7', q.invert('5'));
        assertEquals('6', q.invert('7'));
        assertEquals('4', q.invert('6'));
        assertEquals('J', q.invert('J'));
        assertEquals('H', q.invert('H'));

    }


    @Test
    public void testPermute() {
        Permutation p = new Permutation("(DEVY) (ANS) (H)", alphabet1);
        assertEquals('V', p.permute('E'));
        assertEquals('Y', p.permute('V'));
        assertEquals('D', p.permute('Y'));
        assertEquals('H', p.permute('H'));
        assertEquals('I', p.permute('I'));
        assertEquals('A', p.permute('S'));
        assertEquals('N', p.permute('A'));

        Permutation q = new Permutation("(@123ACB)  (7546) (D!0)", alphabet2);
        assertEquals('1', q.permute('@'));
        assertEquals('7', q.permute('6'));
        assertEquals('H', q.permute('H'));
    }


    @Test
    public void testPermuteInt() {
        Permutation p = new Permutation("(DEVY) (ANS) (H)", alphabet1);
        assertEquals(3, p.invert(9));
        assertEquals(6, p.invert(4));
        assertEquals(8, p.invert(-1));
        assertEquals(5, p.invert(15));
        assertEquals(2, p.invert(3));

        Permutation q = new Permutation("(ABC)", alphabet3);
        assertEquals(1, q.permute(0));
        assertEquals(3, q.permute(-1));
        assertEquals(0, q.permute(-2));
    }

    @Test
    public void testDerangement() {
        Permutation p = new Permutation("(ABC)", alphabet3);
        assertFalse(p.derangement());
        p = new Permutation("(ABC) (D)", alphabet3);
        assertFalse(p.derangement());
        p = new Permutation("(ABCD)", alphabet3);
        assertTrue(p.derangement());
    }
}
