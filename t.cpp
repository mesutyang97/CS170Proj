#include <iostream>
#include <fstream>
#include <cstring>
#include <cstdio>
#include <algorithm>
#include <string>
#include <vector>
#include <cstdlib>
#include <cmath>
#include <ctime>
#define DUMP_COEF 0.3

using namespace std;

int Temperature;
//int f[200000][200000];

struct Node{
	int val, cls, weight, price;
	string s;
};

int n,price, weight, con_num;


vector <Node> item; //list of items (indexed from 0 to n-1)
vector <vector <int> > cst;   //constraint cst[i] the constraints upon class i;
vector <vector <int> > cls; //list of items in a class (indexed )

struct SolInstance{
	vector <bool> sel_item;	// list of selected items boolean;
	vector <int> sel_cls;	// list of selected classes, number of selected items from this clss;
	vector <bool> sel_cst;	// list of selected constraints
	vector <int> ans;		// list of selected items
	int price, weight, val;
	
	void set_conflict(int x, int status){ // class number x; set all its related constraitns to status (1: conflict; 0:non-conflict)
		for (int i=0; i<cst[x].size(); i++){
			sel_cst[cst[x][i]] = status;
		}
	}
 
	bool check_conflict(int x){  //class number x; return 1-> conflic; 0->no conflict
		//cout<<x<<" cst size  "<<cst[x].size()<<endl;
		for (int i=0; i<cst[x].size(); i++){
		//	cout<<"cst num "<<cst[x][i]<<endl;
			if (sel_cst[cst[x][i]]) return 1;
		}
		return 0;
	}

	void add_item(int x){// add item_number x into the solution 
		if (!sel_cls[item[x].cls]){
			set_conflict(item[x].cls, 1);
		}
		sel_cls[item[x].cls] += 1;
		val += item[x].val - item[x].price;
		price += item[x].price;
		weight += item[x].weight;
		sel_item[x] = 1;
	}
	
	void del_item(int x){ //remove item_number x from the solution
		sel_cls[item[x].cls] -= 1;
		if (!sel_cls[item[x].cls]){
			set_conflict(item[x].cls, 0);
		}
		val -= item[x].val - item[x].price;
		price -= item[x].price;
		weight -= item[x].weight;
		sel_item[x] = 0;
	}

	void chosen_items(){
		vector <int> chosen;
	//	cout<<"item size "<<item.size()<<endl;
		for (int i=0; i<item.size(); i++){
			
			if (sel_item[i]){
			//	cout<<i<<endl;				
				chosen.push_back(i);
			}
		}
		ans = chosen;
	}

	void init(){
		while (!ans.empty()){
			ans.pop_back();
		}
		price = 0;
		val = 0;
		weight = 0;
		for (int i=0; i<n; i++){	//initialize the solution instance
			sel_item.push_back(0);
			sel_cls.push_back(0);
		}
		for (int i=0; i<con_num; i++){ //initialize the constraints
			sel_cst.push_back(false);
		}
	}
};


SolInstance FinalAns;

void open_file(){
	freopen("problem21.in", "r", stdin);
	freopen("problem21.out", "w", stdout);
}

void close_file(){
	fclose(stdin);
	fclose(stdout);
}

void read_items(){
	for (int i=0; i<n; i++){ // item input
		string s;
		getline(cin, s);
		s+=';';
		Node cur;
		int t = s.find(';');
		cur.s = s.substr(0,t);
		s.erase(0,t+2);
		float parse[5];
		t = s.find(';');
		int num = 0;
		memset(parse, 0, sizeof(parse));
		while (t!=-1){
			string c = s.substr(0,t);
			parse[num] = atof(c.c_str()), num++;
			s.erase(0,t+2);
			t = s.find(";");
		}
		if (parse[2]>=parse[3]){
			continue;
		}
		else{
			cur.cls = (int) parse[0], cur.weight = (int) (parse[1]*100), cur.price = (int) (parse[2]*100), cur.val =(int) (parse[3]*100);
			item.push_back(cur);
		}
	}
}

void read_const(){ //constraint input
//	cout<<con_num<<endl;
//	string fxk;
//	getline(cin, fxk);
	for (int i=0; i<con_num; i++){
		string s;
		getline(cin, s);
//		cout<<s<<endl;
		s= s + ',';
		int t = s.find(',');	
		int num = 0;
		//cout<<"Cst here"<<endl;
		//cout<<s<<endl;
		while (t!=-1){
			string c = s.substr(0,t);
			num++;
			int temp = atoi(c.c_str());
			//cout<<temp<<' ';
			cst[temp].push_back(i);
			s.erase(0,t+2);
			t = s.find(",");

		}
	}
}

void init(){
	open_file();
	float p, w;
	scanf("%f %f %d %d\n", &p, &w, &n, &con_num);
	//cout<<n<<' '<<con_num<<endl;
	price = (int) (p*100);
	weight = (int) (w*100);
	read_items();
	for (int i=0; i<n; i++){
		vector <int> temp;
		cst.push_back(temp);
		cls.push_back(temp);
	}
	read_const();
	while (!FinalAns.ans.empty()){
		FinalAns.ans.pop_back();
	}
	FinalAns.init();
	Temperature = price;
}



SolInstance init_sol(){
	//generate_cls()
	SolInstance sol;
	sol.init();
	for (int i=0; i<10*n; i++){
		int x = rand()%n;
		if (sol.sel_item[x]){ // current item chosen
			continue;
		}
		if ((sol.weight + item[x].weight > weight) or (sol.price + item[x].price > price)){ // unqualifed
			continue;
		}
		//cout<<"Fine Here"<<endl;
		if ((sol.sel_cls[item[x].cls]) or ((!sol.sel_cls[item[x].cls]) and (!sol.check_conflict(item[x].cls)))){
			sol.add_item(x);	//conflict check
		}
	}
	sol.chosen_items();
	return sol;
}

SolInstance MoveSol(SolInstance cur){
	SolInstance Neighbor;
	Neighbor = cur;
//	Neighbor.chosen_items();
	int total = Neighbor.ans.size();
	int dump_num = max((int) (Neighbor.ans.size() * DUMP_COEF), 1);
	int i = 0;
	//cout<<dump_num<<endl;
	while ((dump_num) and (i< 2*total)){ //remove 30% items
		int x = rand()%total;
		if (Neighbor.sel_item[Neighbor.ans[x]]){
			Neighbor.del_item(Neighbor.ans[x]);
			dump_num--;
		}
	}

	//cout<<"fine after del"<<endl;
	// add items;
	for (i=0; i<2*n; i++){
		int x = rand()%n;
		if (Neighbor.sel_item[x]){ // current item chosen
			continue;
		}
		if ((Neighbor.weight + item[x].weight > weight) or (Neighbor.price + item[x].price > price)){ // unqualifed
			continue;
		}
		//cout<<"Fine Here"<<endl;
		if ((Neighbor.sel_cls[item[x].cls]) or ((!Neighbor.sel_cls[item[x].cls]) and (!Neighbor.check_conflict(item[x].cls)))){
			Neighbor.add_item(x);	//conflict check
		}
	}
	Neighbor.chosen_items();
	return Neighbor;
}


float RandZeroOne(){
	float r = ((float) rand() / (RAND_MAX));
	return r;
}

void SA(){
	SolInstance Current = init_sol();
	for (int i=1; i<=20000; i++){
		SolInstance Neighbor = MoveSol(Current);

		if (Neighbor.val - Current.val > 0){
			Current = Neighbor; 
		}
		else{
			if (exp((Neighbor.val - Current.val)/Temperature) > RandZeroOne()){
				Current = Neighbor;
			}
		}
		if (Current.val > FinalAns.val){
			FinalAns = Current;
		}
	}
}
/*
void test1(){
//	cout<<con_num<<endl;
	cout<<"const given"<<endl;
	for (int i=0; i<n; i++){
		vector <int> temp;
		temp = cst[i];
		//cout<<item[i].s<<' ';
		cout<<"class num"<<item[i].cls<<" ";
		for (int j=0; j<temp.size(); j++){
			 cout<<temp[j]<<' ';
		}
		cout<<endl;
	}
}

void test2(){
	for (int i=0; i<n; i++){
		cout<<i<<' '<<item[i].s<<" "<<endl;
	}
}
*/
void test_final(){
//	FinalAns.chosen_items();
	for (int i=0; i<FinalAns.ans.size(); i++){
		cout<<item[FinalAns.ans[i]].s<<endl;
	}
	cout<<"Final Value: "<<FinalAns.val<<endl;
}


int main(){
	srand(time(NULL));
	init();
	SA();
	test_final();
	return 0;
}

