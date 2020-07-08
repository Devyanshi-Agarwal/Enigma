package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a reflector in the enigma.
 *  @author Devyanshi Agarwal
 */
class Reflector extends FixedRotor {

    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is PERM. */
    Reflector(String name, Permutation perm) {
        super(name, perm);
        _permutation = perm;
    }

    @Override
    boolean reflecting() {
        return true;
    }

    @Override
    int convertForward(int p) {
        if (!_permutation.derangement()) {
            throw EnigmaException.error(
                    "Reflectors must be derangement");
        }
        return _permutation.permute(p);
    }

    @Override
    int convertBackward(int e) {
        throw EnigmaException.error(
                "Reflectors cannot convert backward");
    }

    @Override
    void set(int posn) {
        if (posn != 0) {
            throw error("reflector has only one position");
        }
    }

    /** Returns permutations. */
    private Permutation _permutation;

}
