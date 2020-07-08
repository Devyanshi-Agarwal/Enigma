package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Devyanshi Agarwal
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new enigma.Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine m = readConfig();
        Collection<Rotor> test =  _allRotors;
        String first = _input.nextLine();
        char[] tmp = first.toCharArray();
        if (tmp[0] != '*') {
            throw EnigmaException.error("Incorrect line in config");
        }
        if (_numRotors < _numPawls) {
            throw EnigmaException.error("Num pawls < numRotors");
        }
        setUp(m, first);
        while (_input.hasNextLine()) {
            String next = _input.nextLine();
            if (!next.equals("")) {
                char[] temp = next.toCharArray();
                if (temp[0] == '*') {
                    setUp(m, next);
                    if (_input.hasNextLine()) {
                        next = _input.nextLine();
                    } else {
                        break;
                    }
                }
            }
            printMessageLine(m.convert(next));
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = alpha(_config.next());
            _numRotors = numRotors();
            _numPawls = numPawls(_numRotors);
            _allRotors = new ArrayList<>();
            if (!_config.hasNext()) {
                throw EnigmaException.error("Incorrect config");
            }
            String next = _config.next();
            while (_config.hasNext()) {
                String rotorDesc = "";
                String name = next;
                String description = _config.next();
                String perm = "";
                if (_config.hasNext()) {
                    next = _config.next();
                    while (containsBracket(next)) {
                        if (!_config.hasNext()) {
                            checkPerm(next);
                            perm += next;
                            break;
                        } else {
                            checkPerm(next);
                            perm += next;
                            next = _config.next();
                        }
                    }
                }
                String fullDesc = name + " " + description + " " + perm;
                _allRotors.add(readRotor(fullDesc));
            }
            return new Machine(_alphabet, _numRotors, _numPawls, _allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its ROTORDESCRIPTION from _config. */
    private Rotor readRotor(String rotorDescription) {
        try {
            Scanner rotor = new Scanner(rotorDescription);
            String name = rotor.next();
            String description = rotor.next();
            String permutation = "";
            if (rotor.hasNext()) {
                permutation = rotor.next();
            }
            Permutation perm = new Permutation(permutation, _alphabet);
            char[] descArr = description.toCharArray();
            if (descArr[0] == 'M') {
                String notches = notches(descArr);
                return new MovingRotor(name, perm, notches);
            } else if (descArr[0] == 'N') {
                return new FixedRotor(name, perm);
            } else if (descArr[0] == 'R') {
                return new Reflector(name, perm);
            } else {
                throw EnigmaException.error("wrong rotor type");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String ring = "";
        _plugboard = "";
        Scanner set = new Scanner(settings);
        String[] myRotors = new String[_numRotors];
        set.next();
        for (int i = 0; i < myRotors.length; i++) {
            myRotors[i] = set.next();
        }
        M.insertRotors(myRotors);
        String setting = set.next();
        if (setting.length() > _numRotors || setting.length() < _numPawls) {
            throw EnigmaException.error("incorrect setting");
        }
        while (set.hasNext()) {
            String next = set.next();
            if (!containsBracket(next)) {
                ring = next;
            } else if (checkPlugboard(next)) {
                _plugboard += next;
            }
        }
        M.setRotors(setting, ring);
        Permutation p = new Permutation(_plugboard, _alphabet);
        M.setPlugboard(p);
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        msg = msg.replace(" ", "");
        StringBuilder newMsg = new StringBuilder(msg);
        int len = newMsg.length();
        int spaces = 0;
        for (int i = 0; i < len; i++) {
            if (i != 0 && i % 5 == 0) {
                newMsg.insert(i + spaces, " ");
                spaces += 1;
            }
        }
        _output.println(newMsg);
    }

    /** HELPER FUNCTIONS */

    /**Finds the alphabet by taking in ALPHABET and returns new ALPHABET.*/
    private Alphabet alpha(String alphabet) {
        char[] alpha = alphabet.toCharArray();
        for (char character : alpha) {
            if (character == '(' || character == ')' || character == '*') {
                throw error("Alphabet cannot contain '(', ')' or '*'");
            }
        }
        return new Alphabet(alphabet);
    }
    /**Finds the NUM ROTORS returns NUM.*/
    private int numRotors() {
        if (!_config.hasNextInt()) {
            throw EnigmaException.error("Num rotors not available");
        }
        int num = _config.nextInt();
        if (num <= 0) {
            throw EnigmaException.error("Num rotors cannot be <= 0");
        }
        return num;
    }
    /**Takes in NUMROTORS and returns NUM PAWLS.*/
    private int numPawls(int numRotors) {
        if (!_config.hasNextInt()) {
            throw EnigmaException.error("Num pawls not available");
        }
        int num = _config.nextInt();
        if (num > numRotors) {
            throw EnigmaException.error(
                    "Num pawls cannot be less than numRotors");
        }
        return num;
    }
    /**Checks the INPUT type of permutation.*/
    private void checkPerm(String input) {
        char[] perm = input.toCharArray();
        int length = perm.length;
        char first = perm[0];
        char last = perm[length - 1];
        if (!(first == '(' && last == ')')) {
            throw EnigmaException.error("Incorrect cycle type");
        }
    }
    /**Returns a string of notches by taking in its DESCRIPTION.*/
    private String notches(char[] description) {
        if (description.length == 1) {
            throw EnigmaException.error("Moving rotor has a notch");
        }
        String notch = "";
        for (int i = 1; i < description.length; i++) {
            notch += description[i];
        }
        return notch;
    }
    /**Checks INPUT type pf permutations returns TRUE or FALSE.*/
    private boolean containsBracket(String input) {
        char[] str = input.toCharArray();
        for (char chr : str) {
            if (chr == '(' || chr == ')') {
                return true;
            }
        }
        return false;
    }

    /**Checks the INPUT type of plugboard, returns TRUE or FALSE.*/
    private boolean checkPlugboard(String input) {
        input = input.replace(" ", "");
        checkPerm(input);
        if (input.length() != 4) {
            throw EnigmaException.error("Incorrect plugboard input");
        }
        return true;
    }


    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** Number of rotors used in this machine. */
    private int _numRotors;

    /** Number of pawls used in this machine. */
    private int _numPawls;

    /**Collection of rotors.*/
    private Collection<Rotor> _allRotors;

    /**Machine's plugboard.*/
    private String _plugboard;

}
