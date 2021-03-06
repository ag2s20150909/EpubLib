package me.ag2s.base.okhttp;

import java.util.List;

public class HttpDns {
    public int Status;
    public boolean TC;
    public boolean RD;
    public boolean RA;
    public boolean AD;
    public boolean CD;
    public List<Question> Question;

    /**
     * Question is the inner class of HttpDns
     */
    public class Question {
        public String name;
        public int type;
    }

    public List<Answer> Answer;

    /**
     * Answer is the inner class of HttpDns
     */
    public class Answer {
        public String name;
        public int type;
        public int TTL;
        public String Expires;
        public String data;
    }

}

