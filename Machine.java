package enigma;
import java.util.Collection;

/** Class that represents a complete enigma machine.
 *  @author Devyanshi Agarwal
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        _myRotors = new Rotor[_numRotors];
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        checkDuplicate(rotors);
        _myRotors = new Rotor[_numRotors];
        for (int i = 0; i < rotors.length; i++) {
            for (Rotor thisRotor : _allRotors) {
                if (rotors[i].equals(thisRotor.name())) {
                    _myRotors[i] = thisRotor;
                    break;
                }
            }
        }
        if (!_myRotors[0].reflecting()) {
            throw EnigmaException.error("Reflector"
                     + " in wrong place");
        }
        for (int i = 0; i < _myRotors.length; i++) {
            if (_myRotors[i] == null) {
                throw EnigmaException.error("Bad rotor name");
            }
        }
    }

    /** Set my rotors according to SETTING and RING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting, String ring) {
        char[] tmp = setting.toCharArray();
        for (char temp : tmp) {
            if (!_alphabet.contains(temp)) {
                throw EnigmaException.error(
                        "Setting input not in alphabet");
            }
        }
        if (setting.length() != (numRotors() - 1)) {
            throw EnigmaException.error(
                    "Incorrect input to rotor setting");
        }
        if (ring.equals("")) {
            for (int i = 0; i < setting.length(); i++) {
                ring += Character.toString(_alphabet.toChar(0));
            }
        }
        for (int i = 0; i < setting.length(); i++) {
            _myRotors[i + 1].set(setting.charAt(i));
            _myRotors[i + 1].setRing(ring.charAt(i));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing

     *  the machine. */
    int convert(int c) {
        int input = _plugboard.permute(c);
        boolean[] shouldAdvance = new boolean[numRotors()];
        int numFixed = _numRotors - numPawls();
        for (int i = numFixed; i < shouldAdvance.length; i++) {
            shouldAdvance[i] = true;
        }
        for (int i = numFixed; i < _numRotors; i++) {
            if (shouldAdvance[_numRotors - 1] && i == _numRotors - 1) {
                _myRotors[_numRotors - 1].advance();
                break;
            }
            if (i != _numRotors - 1) {
                if (_myRotors[i + 1].atNotch() && shouldAdvance[i]) {
                    _myRotors[i].advance();
                    shouldAdvance[i] = false;
                    if (shouldAdvance[i + 1]) {
                        _myRotors[i + 1].advance();
                        shouldAdvance[i + 1] = false;
                    }
                }
            }
        }
        for (int i = _numRotors - 1; i >= 0; i = i - 1) {
            input = _myRotors[i].convertForward(input);
        }
        for (int i = 1; i < _numRotors; i++) {
            input = _myRotors[i].convertBackward(input);
        }
        input = _plugboard.invert(input);
        return input;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < msg.length(); i++) {
            char curr = msg.charAt(i);
            if (curr == ' ') {
                output.append(curr);
            } else {
                char tmp = _alphabet.toChar(convert(_alphabet.toInt(curr)));
                output.append(tmp);
            }
        }
        return output.toString();
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** numRotors. */
    private int _numRotors;
    /** numPawls. */
    private int _pawls;
    /** Collection of of my rotors. */
    private Collection<Rotor> _allRotors;
    /** Plugboard. */
    private Permutation _plugboard;
    /** My rotors. */
    private Rotor[] _myRotors;

    /** Checks for duplicates and takes in a string of ROTORS. */
    private void checkDuplicate(String[] rotors) {
        for (int i = 0; i < rotors.length; i++) {
            for (int j = i + 1; j < rotors.length; j++) {
                if (rotors[i].equals(rotors[j])) {
                    throw EnigmaException.error("Duplicate rotor names");
                }
            }
        }
    }
}
