package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Devyanshi Agarwal
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _setting = 0;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _setting;
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        _setting = posn;
    }

    /**Set the setting() to character CPOSN.*/
    void set(char cposn) {
        _setting = _permutation.alphabet().toInt(cposn);
    }

    /** Set the setRing setting() to character CPOSN. */
    void setRing(char cposn) {
        _settingring = _permutation.alphabet().toInt(cposn);
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        int newSetting = _setting - _settingring;
        int contactEntered = _permutation.wrap(p + newSetting);
        int permutation = _permutation.permute((contactEntered));
        int contactExited = _permutation.wrap(permutation - newSetting);
        return contactExited;
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        int newSetting = _setting - _settingring;
        int contactEntered = _permutation.wrap(e + newSetting);
        int permutation = _permutation.invert(contactEntered);
        int contactExited = _permutation.wrap(permutation - newSetting);
        return contactExited;
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    /** the setting of the machine. */
    private int _setting;

    /** setting of the ring.*/
    private int _settingring;
}
