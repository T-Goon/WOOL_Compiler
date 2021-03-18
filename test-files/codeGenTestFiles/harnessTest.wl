class Test
{
    assert(expect : boolean, actual : boolean) : boolean {
        if expect = actual
        then
            true
        else
        {
            abort();
            false;
        }
        fi
    }    run() : boolean {
        {
            assert(true, true);
        }
    }
}
