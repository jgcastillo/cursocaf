function start() {  
        ('startButton1').disable();  
  
        window['progress'] = setInterval(function() {  
            var pbClient = ('pbClient'),  
            oldValue = pbClient.getValue(),  
            newValue = oldValue + 10;  
  
            pbClient.setValue(pbClient.getValue() + 10);  
  
            if(newValue === 100) {  
                clearInterval(window['progress']);  
            }  
  
  
        }, 1000);  
    }  
  
    function cancel() {  
        clearInterval(window['progress']);  
        ('pbClient').setValue(0);  
        ('startButton1').enable();  
    }  