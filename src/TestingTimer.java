

import java.util.concurrent.TimeUnit;

abstract class TestingTimer
{
    /**
     * The execution time of the last run.
     */
    private Long executionTime = 0L;
    
    abstract Object functionToRun();
    
    public Object timeFunction()
    {
        Long start = System.nanoTime();
        
        Object result = functionToRun();
        
        Long end = System.nanoTime();
        executionTime = end - start;
        
        return result;
    }
    
    /**
     * @return Latest time in nanoseconds.
     */
    public Long getLatestTime()
    {
        return executionTime;
    }
    
    public Long getLatestTime(TimeUnit unit)
    {   
        Long result = executionTime;
        switch(unit)
        {
        case DAYS: result = result / 24;
        case HOURS: result = result / 60;
        case MINUTES: result = result / 60;
        case SECONDS: result = result / 1000;
        case MILLISECONDS: result = result / 1000;
        case MICROSECONDS: result = result / 1000;
        default:
        }
        return result;
    }
}
