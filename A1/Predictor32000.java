
public class Predictor32000 extends Predictor {

	Table phr;
	Register ghr;

	
	public Predictor32000() {
		this.phr = new Table(64, 472);
		this.ghr = new Register(59);
		this.ghr.setBitAtIndex(0, true);
	}

	public void Train(long address, boolean outcome, boolean predict) {
		int PC = (1 << 6);
		PC -= 1;
		PC = (int)(PC&address);
		if(outcome != predict){
			//if(outcome == true){
			for(int i= 0; i< 472; i+=8){
				int weight = phr.getInteger(PC, i, i+7);
				boolean gh = ghr.getBitAtIndex(i/8);
				if(outcome == gh){
					if(weight < 256){
						phr.setInteger(PC, i, i+7, weight+1);
					}
				}
				else{
					if(weight > 0){
						phr.setInteger(PC, i, i+7, weight-1);
					}
				}	
				
			}
			
		}

		int temp = ghr.getInteger(1, 58);
		temp = (temp >> 1);
		if(outcome == true){
			temp += (1 << 57);
		}
		ghr.setInteger(1, 58, temp);
	}


	public boolean predict(long address){

		int PC = (1 << 6);
		PC -= 1;
		PC = (int)(PC&address);
		//int res = phr.getInteger(PC, 0, 2);
		int res = phr.getInteger(PC, 0, 7);
		for(int i= 8; i< 472; i+=8){
			int temp = phr.getInteger(PC, i, i+7);
			boolean gh = ghr.getBitAtIndex(i/8);
			if(gh == true){
				res += temp;
			}
			else{
				res -= temp;
			}

		}
		if(res >= 0){
			return true;
		}
		else{
			return false;
		}
	}

}
