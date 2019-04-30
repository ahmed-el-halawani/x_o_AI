package com.example.x_o_ai

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.gamemode.*
import kotlinx.android.synthetic.main.pause.*
import kotlinx.android.synthetic.main.wld.*
import java.util.ArrayList


class MainActivity : AppCompatActivity() {


    private var player = "x"
    private var endGame = 0
    private var gameMode = 1
    private var onePlayer = 1
    private var twoPlayer = 2
    private var isFirstMove = true

    // view fun ///////////
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        skip.setOnClickListener {
            wldLayout.visibility = View.INVISIBLE
            Player1.visibility = View.INVISIBLE
            Player2.visibility = View.INVISIBLE
            drow.visibility = View.INVISIBLE
            reset()
        }

    }
    
    fun pauseGame(view:View){
        val pauseGame = view as Button
        when(pauseGame){
            pouse -> pauseGameLayout.visibility = View.VISIBLE
            Continue -> pauseGameLayout.visibility = View.GONE
            changeMode -> changeMode()
        }
    }

    private fun changeMode(){
        pauseGameLayout.visibility = View.GONE
        playerSelectLayout.visibility = View.VISIBLE
    }
    
    fun playerSelect(view:View){
        val selectMode = view as Button
        gameMode = if(selectMode == one_player){
            onePlayer
        }else{
            twoPlayer
        }
        playerSelectLayout.visibility = View.GONE
        reset()
    }

    private fun shawWinner(state:String){
        when(state){
            "x" ->{
                wldLayout.visibility = View.VISIBLE
                Player1.visibility = View.VISIBLE
                wldLayout.setBackgroundResource(R.color.player1)
                endGame()
            }
            "o" ->{
                wldLayout.visibility = View.VISIBLE
                Player2.visibility = View.VISIBLE
                wldLayout.setBackgroundResource(R.color.player2)
                endGame()
            }
            else ->{
                wldLayout.visibility = View.VISIBLE
                drow.visibility = View.VISIBLE
                wldLayout.setBackgroundResource(R.color.drow)
                endGame()
            }
        }
    }

    private fun changeBtnText(btn:Button){
        if (player == "x") {
            btn.text = "x"
            btn.setBackgroundResource(R.color.blue)
            btn.isEnabled = false
            //autoPlay()

        } else {
            btn.text = "o"
            btn.setBackgroundResource(R.color.darkgreen)
            btn.isEnabled = false
        }

    }
    
    // end view fun /////////

    
    private fun currentBord():ArrayList<Button>{
        val currentBord: ArrayList<Button> = ArrayList()
        currentBord.add(btn1)  ;currentBord.add(btn2)  ;currentBord.add(btn3)
        currentBord.add(btn4)  ;currentBord.add(btn5);currentBord.add(btn6)
        currentBord.add(btn7)  ;currentBord.add(btn8)  ;currentBord.add(btn9)
        return currentBord
    }
    
    fun btnSelect(view: View){
        val chooseBtn = view as Button
        if(chooseBtn == res){
            reset()
        }else if (gameMode == onePlayer){
            startGame(chooseBtn)
            if (isFirstMove) {
                isFirstMove=false

                if (empty(currentBord()).contains(4))
                {
                    startGame(currentBord()[4])
                }

                else if(empty(currentBord()).contains(0)&&
                    empty(currentBord()).contains(2)&&
                    empty(currentBord()).contains(6)&&
                    empty(currentBord()).contains(8))
                {
                    startGame(currentBord()[arrayOf(0,2,6,8).random()])
                }

            }else{
                aiTurn()
            }
        }else if (gameMode == twoPlayer){
            startGame(chooseBtn)
        }

    }

    private fun empty(bord: ArrayList<Button>): ArrayList<Int> {
        val list: ArrayList<Int> = ArrayList()
        for (i in bord) {
            if (!(i.text == "x" || i.text == "o")) {
                list.add(bord.indexOf(i))
            }
        }
        return list
    }

    private fun winner(bord: ArrayList<Button>, players: String): Boolean {
        for (i in 0..2) {
            if (
                (bord[i * 3].text == players && bord[(i * 3) + 1].text == players && bord[(i * 3) + 2].text == players) ||
                (bord[i].text == players && bord[i + 3].text == players && bord[i + 6].text == players)
            ) {
                return true
            }
        } //col and raw

        // diagonal
        if ((bord[0].text == players && bord[4].text == players && bord[8].text == players) ||
            (bord[2].text == players && bord[4].text == players && bord[6].text == players))
        {
            return true
        }


        return false
    }
    
    private fun startGame(btn:Button){
        val currentBord = currentBord()
        if(!empty(currentBord).isEmpty()){
            changeBtnText(btn)
            if (winner(currentBord, player)) {
                shawWinner(player)
            }
            player = when(player){
                "x" -> "o"
                else -> "x"
            }
        }else {
            shawWinner("drow")
        }
    }

    private fun aiTurn(){
        val currentBord = currentBord()
        if(!empty(currentBord).isEmpty()){
            changeBtnText(ai())
            if (winner(currentBord, "o")) {
                shawWinner(player)
            }
            player = when(player){
                "x" -> "o"
                else -> "x"
            }
        }else{
            shawWinner("drow")
        }
    }

    private fun endGame(){
        endGame = 1
        for (i in currentBord()){
            i.isEnabled = false
        }
    }

    private fun reset(){
        isFirstMove = true
        player = "x"
        endGame = 0
        for (i in currentBord()){
            i.text = ""
            i.setBackgroundResource(R.color.gray)
            i.isEnabled = true
        }
    }


    //////////////////////////// AI //////////////////////////////
    private fun ai():Button{

        class ChicArray{
            var index =0
            var score =0
        }

        val currentBord = currentBord()

        fun minimax(bord:ArrayList<Button>, player:String):ChicArray {


            val availoblespits = empty(bord)

            if (winner(bord, "x")) {
                val result = ChicArray()
                result.score = -10
                return result
            }else if (winner(bord, "o") ) {
                val result = ChicArray()
                result.score = 10
                return result
            }else if(availoblespits.size == 0){
                val result = ChicArray()
                result.score = 0
                return result
            }

            val allsecondChic = ArrayList<ChicArray>()

            for (i in availoblespits) {
                val x = ChicArray()
                x.index = i
                val btntext = bord[i].text
                bord[i].text = player

                if (player == "o") {
                    val result = minimax(bord, "x")
                    x.score = result.score
                }
                if (player == "x") {
                    val result = minimax(bord, "o")
                    x.score = result.score
                }


                bord[i].text = btntext

                allsecondChic.add(x)
            }

            var bestmove = 0
            if (player == "o") {

            var bestscore = -10000
            for (i in allsecondChic) {
                if (i.score > bestscore) {
                    bestscore = i.score
                    bestmove = allsecondChic.indexOf(i)
                }
            }

            } else if (player == "x") {

                var bestscore = 10000
                for (i in allsecondChic) {
                    if (i.score < bestscore) {
                        bestscore = i.score
                        bestmove = allsecondChic.indexOf(i)
                    }
                }
            }
            return allsecondChic[bestmove]
        }




        fun update():Button {
            val bestMove = minimax(currentBord,player).index
            return currentBord[bestMove]
        }
        return update()
    }

}
