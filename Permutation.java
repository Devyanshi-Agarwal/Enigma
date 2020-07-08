package enigma;

import java.util.HashMap;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Devyanshi Agarwal
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
        _cycles = _cycles.replace(" ", "");
        _cycles = _cycles.replace(")", " ");
        _cycles = _cycles.replace("(", "");
        _permutations = new HashMap<>();
        _inverts = new HashMap<>();
        addCycle(_cycles);
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        char[] charArray = _cycles.toCharArray();
        if (charArray.length == 0) {
            for (int i = 0; i < _alphabet.size(); i++) {
                _permutations.put(_alphabet.toChar(i), _alphabet.toChar(i));
            }
        } else {
            char first = charArray[0];
            for (int i = 0; i < charArray.length; i++) {
                char curr = charArray[i];
                if (i == charArray.length - 1 && curr == ' ') {
                    break;
                }
                if (curr == ' ') {
                    first = charArray[i + 1];
                } else if (charArray[i + 1] == ' ') {
                    _permutations.put(curr, first);
                    _inverts.put(first, curr);
                    first = curr;
                } else {
                    _permutations.put(curr, charArray[i + 1]);
                    _inverts.put(charArray[i + 1], curr);
                }

            }
        }
    }

    /** checks the cycle taking CHARACTER C. */
    private void checkCycle(char c) {
        char input = c;
        if (input != ' ') {
            if (!_alphabet.contains(input)) {
                throw EnigmaException.error("Invalid input");
            }
        }
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the RESULT of applying this permutation
     * to P modulo the alphabet size. */
    int permute(int p) {
        int index = wrap(p);
        char input = _alphabet.toChar(index);
        char output = permute(input);
        int val = _alphabet.toInt(output);
        return val;
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int index = wrap(c);
        char input = _alphabet.toChar(index);
        char output = invert(input);
        int val = _alphabet.toInt(output);
        return val;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        checkCycle(p);
        Character val = p;
        if (_permutations.containsKey(p)) {
            val = _permutations.get(p);
        }
        return val;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        checkCycle(c);
        Character val = c;
        if (_inverts.containsKey(c)) {
            val = _inverts.get(c);
        }
        return val;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < _alphabet.size(); i++) {
            if (!_permutations.containsKey(_alphabet.toChar(i))) {
                return false;
            }
            if (_permutations.get(_alphabet.toChar(i)) == _alphabet.toChar(i)) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
    /** Cycles of this permutation. */
    private String _cycles;
    /** Hashmap of permutations. */
    private HashMap<Character, Character> _permutations;
    /** Hashmap of inverts. */
    private HashMap<Character, Character> _inverts;
}
