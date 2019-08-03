//获取最大盒子
    var box = document.getElementById("box");
    //获取小盒子
    var small = document.getElementById("small");
    //获取mask
    var mask = document.getElementById("mask");
    //获取大盒子
    var big = document.getElementById("big");
 
    //设置小盒子的移入事件  移入显示，移出隐藏
    small.onmouseover = function(){
        mask.style.display = "block";
        big.style.display = "block";
    }
    small.onmouseout = function(){
        mask.style.display = "none";
        big.style.display = "none";
    }
 
    //要得到鼠标在 small 内的坐标
    small.onmousemove = function(event){
        event = event || window.event;
        var pagex = event.pageX || scroll().left + event.clientX;
        var pagey = event.pageY || scroll().top + event.clientY;
        //x,y表示mask的位置
        var x = pagex - box.offsetLeft - mask.offsetWidth/2;
        var y = pagey - box.offsetTop - mask.offsetHeight/2;
        //限制盒子的移动范围
        if (x<0){
            x = 0;
        }else if(x > small.offsetWidth - mask.offsetWidth){
            x = small.offsetWidth - mask.offsetWidth;
        }
 
        if (y<0){
            y = 0;
        }else if(y > small.offsetHeight - mask.offsetHeight){
            y = small.offsetHeight - mask.offsetHeight;
        }
 
        //移动黄盒子
        mask.style.left = x + "px";
        mask.style.top = y + "px";
 
        //等比例移动右边的盒子
        //获取大图片
        var bigImg = big.children[0];
        //大盒子走的距离/mask走的距离 = (大图片 - 大盒子)/(小图片 - 黄盒子)
        //比例公式  大图片宽度 - 大盒子宽度/ 小盒子宽度 - mask宽度
//        var eqs = big.offsetWidth - bigImg.offsetWidth / small.offsetWidth - mask.offsetWidth;
        var eqs = (bigImg.offsetWidth - big.offsetWidth) / (small.offsetWidth - mask.offsetWidth);
        //大图片移动
        bigImg.style.marginLeft = -x*eqs + "px";
        bigImg.style.marginTop = -y*eqs + "px";
 
    }
 
    function scroll(){
        return {
            "top": window.pageYOffset || document.body.scrollTop || document.documentElement.scrollTop,
            "left":  window.pageXOffset || document.body.scrollLeft || document.documentElement.scrollLeft
        }
    }