package enigma;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Devyanshi Agarwal
 */
class Alphabet {

    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _chars = chars;
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _chars.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        for (int i = 0; i < size(); i++) {
            if (_chars.charAt(i) == ch) {
                return true;
            }
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (0 > index || index >= size()) {
            throw EnigmaException.error("out of range");
        }
        return _chars.charAt(index);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        for (int i = 0; i < size(); i++) {
            if (_chars.charAt(i) == ch) {
                return i;
            }
        }
        throw EnigmaException.error("out of range");
    }

    /** String of characters.*/
    private String _chars;
}
