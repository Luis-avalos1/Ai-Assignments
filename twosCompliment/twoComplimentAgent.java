
public class twoComplimentAgent {
    
    // our binary string
    String binaryString;
    int len;
    // result array
    char[] twoCompliment;



    // constructor 
    public twoComplimentAgent(String binaryString){
        this.binaryString = binaryString;
        this.len = binaryString.length();
        this.twoCompliment = new char[len];
    }

    public String stateProcess(){
        // we will convert binary string to char array for easier traversal
        char [] currentBinary= binaryString.toCharArray();
        // boolean if we encounter a 1 
        boolean firstOneEncountered = false;

        // State S0: scan from right to left
        for (int i = len-1; i >= 0; i--) {
            
            // if the first '1' has been encountered
            if(firstOneEncountered) 
            {   
                // [AFTER S2: WE HAVE ALREADY ENCOUNTERED THE FIRST 1 SO S3]
                // S3: Each subsequent bit we will invert it ---> if 0 then 1 : if 1 then 0 ::: remain to S3
                twoCompliment[i] = (currentBinary[i]=='0') ? '1' : '0';

            }
            
            // if no '1' , then must be '0', SO S1: If 0, remain in S1
            else 
            {
                // S1 || S2: transition to S2 when encounter first '1'
                twoCompliment[i] = currentBinary[i];

                // if firstOneEncountered switch boolean to activate if condition and transition to S3
                if(currentBinary[i] == '1'){
                    firstOneEncountered = true;
                }

            }

        } // end of for loop

        // S4: all bits processed --> return twos compliment of binary string
        return new String(twoCompliment);
    }



    
    public static void main(String[] args) {

        // input
        String binaryString = "01010100010";
        twoComplimentAgent agent = new twoComplimentAgent(binaryString);
        String twoC = agent.stateProcess();

        // output
        System.out.println("""
                                         OUTPUT
                           --------------------------------""");
        System.out.println("Binary String: " + binaryString);
        System.out.println("Two's Compliment: " + twoC);


    }

}