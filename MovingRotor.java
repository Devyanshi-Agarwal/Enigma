package enigma;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Devyanshi Agarwal
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _permutation = perm;
        _notches = notches;
        _notchesArr = _notches.toCharArray();
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        char curr = alphabet().toChar(setting());
        for (char n : _notchesArr) {
            if (n == curr) {
                return true;
            }
        }
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    @Override
    void advance() {
        super.set(_permutation.wrap(setting() + 1));
    }

    /** Permutation of the rotor.*/
    private Permutation _permutation;
    /** Notches of the rotor.*/
    private String _notches;
    /** setting of the rotor.*/
    private int _setting;
    /** notches of the rotor.*/
    private char[] _notchesArr;
}
