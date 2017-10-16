package net.neferett.linaris.utils.stringutils;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Splitter;

public class StringUtil {

	public static String join(String[] arr, String separator) {
		StringBuilder sb = new StringBuilder();
		if(arr.length == 0) return "";
		else if(arr.length == 1) sb.append(arr[0]);
		else {
			for(int i = 0; i < arr.length - 1; i ++) {
				if(i != 0) sb.append(separator);
				sb.append(arr[i]);
			}
		}
		return sb.toString();
	}
	
	private static boolean do_miniglob(List<String> pattern, String line) {
        if (pattern.size() == 0)
            return line.isEmpty();
        if (pattern.size() == 1)
            return line.equals(pattern.get(0));
        if (!line.startsWith(pattern.get(0)))
            return false;
 
        int idx = pattern.get(0).length();
        String patternTok;
        int nextIdx;
        for (int i = 1; i < pattern.size() - 1; ++i) {
            patternTok = pattern.get(i);
            nextIdx = line.indexOf(patternTok, idx);
            if (nextIdx < 0)
                return false;
            idx = nextIdx + patternTok.length();
        }
        return line.endsWith(pattern.get(pattern.size() - 1));
 
    }
 
    public static boolean miniglob(String pattern, String line) {
        // miniglob : parseur de permissions, avec support léger pour les wildcard :)
        // ("a.b.c", "a.b.c") -> true
        // ("a.*", "a.b.c") -> true
        return do_miniglob(Splitter.on('*').splitToList(pattern), line);
    }
 
    public static boolean miniglob(Collection<String> patterns, String line) {
        if (patterns.contains(line))
            return true;
        for (String pattern : patterns) {
            if (miniglob(pattern, line)) {
                return true;
            }
        }
        return false;
    }
	
}